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
  interface Unsafe {
    void flush();

    void beginRead();

    void read() throws IOException;
  }

  SelectableChannel javaChannel();

  Pipeline pipeline();

  EventLoop eventLoop();

  CompletableFuture<Void> register(final EventLoop eventLoop);

  CompletableFuture<Void> deregister();

  CompletableFuture<Void> bind(final SocketAddress localAddress);

  CompletableFuture<Void> write(final ByteBuffer buffer);

  CompletableFuture<Void> close();

  Unsafe unsafe();
}
