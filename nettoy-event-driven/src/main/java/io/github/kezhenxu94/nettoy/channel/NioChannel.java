package io.github.kezhenxu94.nettoy.channel;

import lombok.extern.java.Log;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kezhenxu94
 */
@Log
public class NioChannel extends AbstractNioChannel {
  private final Map<CompletableFuture<Void>, ByteBuffer> pendingWrites;

  public NioChannel(final SelectableChannel javaChannel) throws IOException {
    super(javaChannel);
    this.pendingWrites = new ConcurrentHashMap<>();
    this.unsafe = new Unsafe();
  }

  @Override
  public CompletableFuture<Void> bind(final SocketAddress localAddress) {
    throw new UnsupportedOperationException();
  }

  @Override
  public CompletableFuture<Void> write(final ByteBuffer buffer) {
    LOGGER.info("writing message: " + buffer);

    final var future = new CompletableFuture<Void>();

    pendingWrites.put(future, buffer);

    return future;
  }

  @Override
  public CompletableFuture<Void> close() {
    final var future = new CompletableFuture<Void>();

    return deregister().thenComposeAsync(ignored -> {
      try {
        javaChannel().close();
        future.complete(null);
      } catch (IOException e) {
        future.completeExceptionally(e);
      }
      return future;
    }).exceptionallyCompose(throwable -> {
      future.completeExceptionally(throwable);
      return future;
    });
  }

  class Unsafe implements Channel.Unsafe {
    @Override
    public void read() throws IOException {
      final var channel = (SocketChannel) javaChannel();
      final var buffer = ByteBuffer.allocate(1024);
      channel.read(buffer);

      pipeline().channelRead(buffer);
    }

    @Override
    public void flush() {
      for (final var it = pendingWrites.keySet().iterator(); it.hasNext(); it.remove()) {
        final var future = it.next();
        final var buffer = pendingWrites.get(future);
        try {
          LOGGER.info("writing to channel " + this + ": " + buffer);

          ((SocketChannel) javaChannel()).write(buffer);

          future.complete(null);
        } catch (IOException e) {
          future.completeExceptionally(e);
        }
      }
    }

    @Override
    public void beginRead() {
      final var interestOps = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
      eventLoop().execute(() -> selectionKey.interestOps(selectionKey.interestOps() | interestOps));
    }
  }
}
