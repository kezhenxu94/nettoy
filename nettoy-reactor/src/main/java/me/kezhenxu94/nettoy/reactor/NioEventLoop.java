package me.kezhenxu94.nettoy.reactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * Created by kezhenxu94 in 2019-02-03 23:20
 *
 * @author kezhenxu94 (kezhenxu94 at 163 dot com)
 */
public class NioEventLoop {
  private final Selector selector;

  private volatile boolean started;

  public NioEventLoop() throws IOException {
    this.selector = Selector.open();
  }

  public Selector selector() {
    return selector;
  }

  public synchronized void start() {
    if (started) {
      throw new IllegalStateException("EventLoop has started");
    }
    started = true;
    new Thread(() -> {
      for (; ; ) {
        try {
          selector.select(1000);
          processSelectedKeys();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  private void processSelectedKeys() {
    for (Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
         iterator.hasNext();
         iterator.remove()) {
      final SelectionKey key = iterator.next();
      final AbstractNioChannel nioChannel = (AbstractNioChannel) key.attachment();
      if (key.isAcceptable()) {
        nioChannel.handler().onChannelAcceptable(nioChannel);
      }
      if (key.isWritable()) {
        nioChannel.handler().onChannelWritable(nioChannel);
      }
      if (key.isReadable()) {
        nioChannel.handler().onChannelReadable(nioChannel);
      }
    }
  }
}
