package io.github.kezhenxu94.nettoy;

import io.github.kezhenxu94.nettoy.reactor.ChannelHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * @author kezhenxu94
 */
public final class EchoHandler implements ChannelHandler {
  static final Logger LOGGER = Logger.getLogger(EchoHandler.class.getName());
  static final String POISON_PILL = "BYE";

  final Executor executor;
  final SocketChannel socketChannel;
  final MsgCodec msgCodec;
  final Selector selector;
  final LinkedBlockingQueue<String> msgQ;

  public EchoHandler(final SocketChannel socketChannel, final Selector selector) throws IOException {
    this.socketChannel = socketChannel;
    this.msgCodec = new MsgCodec();
    this.selector = selector;
    this.msgQ = new LinkedBlockingQueue<>();
    this.executor = Executors.newCachedThreadPool(r -> {
      final Thread thread = new Thread(r);
      thread.setName("Worker");
      return thread;
    });

    socketChannel.configureBlocking(false);
    socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE).attach(this);

    selector.wakeup();
  }

  @Override
  public void read() throws Exception {
    final ByteBuffer buffer = ByteBuffer.allocate(1024);
    socketChannel.read(buffer);
    final String msg = msgCodec.decode(buffer);
    LOGGER.info("[" + Thread.currentThread() + "] <=== " + msg);

    executor.execute(new Processor(msg));
  }

  @Override
  public void write() throws Exception {
    if (msgQ.isEmpty()) {
      socketChannel.register(selector, SelectionKey.OP_READ).attach(this);
      return;
    }
    final String msg = msgQ.take();
    final ByteBuffer buffer = msgCodec.encode(msg);
    socketChannel.write(buffer);
    LOGGER.info("[" + Thread.currentThread() + "] ===> " + msg);

    if (POISON_PILL.equals(msg)) {
      LOGGER.info("Closing " + socketChannel);
      socketChannel.close();
    }
  }

  class Processor implements Runnable {
    private final String message;

    Processor(final String message) {
      this.message = message;
    }

    @Override
    public void run() {
      try {
        LOGGER.info("[" + Thread.currentThread() + "] is handling message [" + message + "]");
        Thread.sleep(3000L); // Do some time-consuming computations
        msgQ.put(message);
        socketChannel.register(selector, SelectionKey.OP_WRITE).attach(EchoHandler.this);
        selector.wakeup(); // Wake up the selector because it may be waiting for READ events, but we're not interested in READ now
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }
  }

  static class MsgCodec {
    ByteBuffer encode(final String msg) {
      return ByteBuffer.wrap(msg.getBytes());
    }

    String decode(final ByteBuffer buffer) {
      return new String(buffer.array(), buffer.arrayOffset(), buffer.remaining());
    }
  }
}