package io.github.kezhenxu94.nettoy.channel;

import lombok.extern.java.Log;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kezhenxu94
 */
@Log
public class NioChannel extends AbstractNioChannel {
  private final Map<CompletableFuture<Throwable>, ByteBuffer> pendingWrites;

  public NioChannel(final SelectableChannel javaChannel) throws IOException {
    super(javaChannel);
    this.pendingWrites = new ConcurrentHashMap<>();
  }

  @Override
  public CompletableFuture<Throwable> bind(final SocketAddress localAddress) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void read() throws IOException {
    final SocketChannel channel = (SocketChannel) javaChannel();
    final ByteBuffer buffer = ByteBuffer.allocate(1024);
    channel.read(buffer);

    pipeline().channelRead(buffer);
  }

  @Override
  public CompletableFuture<Throwable> write(final ByteBuffer buffer) {
    LOGGER.info("writing message: " + buffer);

    final CompletableFuture<Throwable> future = new CompletableFuture<>();

    pendingWrites.put(future, buffer);

    return future;
  }

  @Override
  public void flush() {
    for (final Iterator<CompletableFuture<Throwable>> it = pendingWrites.keySet()
                                                                        .iterator(); it.hasNext(); it.remove()) {
      final CompletableFuture<Throwable> future = it.next();
      final ByteBuffer buffer = pendingWrites.get(future);
      try {
        LOGGER.info("writing to channel " + this + ": " + buffer);

        ((SocketChannel) javaChannel()).write(buffer);

        future.complete(null);
      } catch (IOException e) {
        future.complete(e);
      }
    }
  }

  @Override
  public void beginRead() {
    final int interestOps = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
    eventLoop().execute(() -> selectionKey.interestOps(selectionKey.interestOps() | interestOps));
  }

  @Override
  public void close() {
    try {
      deregister();
      javaChannel().close();
    } catch (IOException e) {
      throw new RuntimeException("failed to close channel", e);
    }
  }
}
