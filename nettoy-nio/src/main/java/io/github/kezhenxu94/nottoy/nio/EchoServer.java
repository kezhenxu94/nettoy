package io.github.kezhenxu94.nottoy.nio;

import lombok.extern.java.Log;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by kezhenxu94 in 2018/8/28 21:53
 *
 * @author kezhenxu94 (kezhenxu94 at 163 dot com)
 */
@Log
public class EchoServer {
  private static final String POISON_PILL = "BYE";

  public void start() throws Exception {
    try (final var serverSocketChannel = ServerSocketChannel.open()) {
      final var selector = Selector.open();

      serverSocketChannel.bind(new InetSocketAddress(8080));
      serverSocketChannel.configureBlocking(false);
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

      while (!Thread.interrupted()) {
        if (selector.select(1000L) == 0) {
          continue;
        }
        for (final var it = selector.selectedKeys().iterator(); it.hasNext(); it.remove()) {
          final var key = it.next();
          if (key.isAcceptable()) {
            final var server = (ServerSocketChannel) key.channel();
            final var client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, ByteBuffer.allocate(1024));
            LOGGER.info("client connected: " + client);
          }
          if (key.isReadable()) {
            readData(key);
          }
          if (key.isWritable()) {
            writeData(key);
          }
        }
      }
    }
  }

  private void writeData(final SelectionKey key) throws Exception {
    final var channel = (SocketChannel) key.channel();
    final var buffer = (ByteBuffer) key.attachment();

    try {
      buffer.flip();
      if (!buffer.hasRemaining()) {
        return;
      }

      final var s = new String(buffer.array(), buffer.arrayOffset(), buffer.remaining()).trim();
      LOGGER.info("===> " + s);

      channel.write(buffer);

      if (s.equals(POISON_PILL)) {
        channel.close();
        LOGGER.info(channel + " closed");
      }
    } finally {
      buffer.clear();
    }
  }

  private void readData(final SelectionKey key) throws Exception {
    final var buffer = ((ByteBuffer) key.attachment());
    final var channel = (SocketChannel) key.channel();
    final var read = channel.read(buffer);
    if (read <= 0) {
      return;
    }
    final var s = new String(buffer.array(), 0, read).trim();
    LOGGER.info("<=== " + s);
  }

  public static void main(String[] args) throws Exception {
    new EchoServer().start();
  }
}
