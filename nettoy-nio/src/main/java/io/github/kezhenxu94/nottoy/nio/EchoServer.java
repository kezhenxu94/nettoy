package io.github.kezhenxu94.nottoy.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by kezhenxu94 in 2018/8/28 21:53
 *
 * @author kezhenxu94 (kezhenxu94 at 163 dot com)
 */
public class EchoServer {
  private static final Logger LOGGER = Logger.getLogger(EchoServer.class.getName());
  private static final String POISON_PILL = "BYE";

  public void start() throws Exception {
    try (final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
      final Selector selector = Selector.open();

      serverSocketChannel.bind(new InetSocketAddress(8080));
      serverSocketChannel.configureBlocking(false);
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

      while (!Thread.interrupted()) {
        if (selector.select(1000L) == 0) {
          continue;
        }
        for (final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); iterator.hasNext(); iterator.remove()) {
          final SelectionKey key = iterator.next();
          if (key.isAcceptable()) {
            final ServerSocketChannel server = (ServerSocketChannel) key.channel();
            final SocketChannel client = server.accept();
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
    final SocketChannel channel = (SocketChannel) key.channel();
    final ByteBuffer buffer = (ByteBuffer) key.attachment();

    try {
      buffer.flip();
      if (!buffer.hasRemaining()) {
        return;
      }

      final String s = new String(buffer.array(), buffer.arrayOffset(), buffer.remaining()).trim();
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
    final ByteBuffer buffer = ((ByteBuffer) key.attachment());
    final SocketChannel channel = (SocketChannel) key.channel();
    final int read = channel.read(buffer);
    if (read <= 0) {
      return;
    }
    final String s = new String(buffer.array(), 0, read).trim();
    LOGGER.info("<=== " + s);
  }

  public static void main(String[] args) throws Exception {
    new EchoServer().start();
  }
}
