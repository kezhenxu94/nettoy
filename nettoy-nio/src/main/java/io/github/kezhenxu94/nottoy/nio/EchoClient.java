package io.github.kezhenxu94.nottoy.nio;

import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by kezhenxu94 in 2018/8/28 21:58
 *
 * @author kezhenxu94 (kezhenxu94 at 163 dot com)
 */
@Log
public class EchoClient {
  private static final String POISON_PILL = "BYE";

  private final Queue<String> messageQueue = new ConcurrentLinkedQueue<>();

  private volatile boolean running = true;

  public void start() {
    final Thread thread = new Thread(() -> {
      try (final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
          messageQueue.offer(line);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    thread.setDaemon(true);
    thread.start();

    try (final SocketChannel socketChannel = SocketChannel.open();
         final Selector selector = Selector.open()) {
      socketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost(), 8080));
      socketChannel.configureBlocking(false);
      socketChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);

      while (running) {
        if (selector.select(1000L) <= 0) {
          continue;
        }
        for (Iterator<SelectionKey> iterator = selector.selectedKeys()
                                                       .iterator(); iterator.hasNext(); iterator.remove()) {
          final SelectionKey key = iterator.next();
          if (key.isReadable()) {
            readData(key);
          }
          if (key.isWritable()) {
            writeData(key);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void readData(SelectionKey key) throws Exception {
    final SocketChannel channel = (SocketChannel) key.channel();
    final ByteBuffer buffer = ByteBuffer.allocate(1024);
    final int read = channel.read(buffer);
    if (read <= 0) {
      return;
    }
    final String s = new String(buffer.array(), 0, read);
    LOGGER.info("<=== " + s);
    if (POISON_PILL.equals(s.trim())) {
      running = false;
    }
  }

  private void writeData(SelectionKey key) throws Exception {
    final SocketChannel channel = (SocketChannel) key.channel();
    final String line = messageQueue.poll();
    if (line == null) {
      return;
    }
    final ByteBuffer buffer = ByteBuffer.allocate(1024);
    buffer.put(line.getBytes());
    buffer.flip();
    channel.write(buffer);
    LOGGER.info("===> " + line);
  }

  public static void main(String[] args) {
    new EchoClient().start();
  }
}
