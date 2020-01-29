package io.github.kezhenxu94.nettoy.channel;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.util.concurrent.CompletableFuture;

/**
 * @author kezhenxu94
 */
public interface Channel {
  SelectableChannel javaChannel();

  CompletableFuture<Throwable> register(final EventLoop eventLoop);

  CompletableFuture<Throwable> deregister();

  Pipeline pipeline();

  CompletableFuture<Throwable> bind(final SocketAddress localAddress);

  EventLoop eventLoop();

  void read() throws IOException;

  CompletableFuture<Throwable> write(final ByteBuffer buffer);

  void flush();

  void beginRead();

  void close();
}
