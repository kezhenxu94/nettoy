package io.github.kezhenxu94.nettoy.channel;

import lombok.Getter;
import lombok.extern.java.Log;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

/**
 * @author kezhenxu94
 */
@Log
public abstract class AbstractNioChannel implements Channel {
  @Getter
  private final SelectableChannel javaChannel;
  @Getter
  private final Pipeline pipeline;
  @Getter
  protected Unsafe unsafe;

  protected EventLoop eventLoop;
  protected volatile SelectionKey selectionKey;

  public AbstractNioChannel(final SelectableChannel javaChannel) throws IOException {
    this.javaChannel = requireNonNull(javaChannel, "javaChannel");
    this.javaChannel.configureBlocking(false);
    this.pipeline = new DefaultPipeline(this);
  }

  @Override
  public CompletableFuture<Void> register(final EventLoop eventLoop) {
    LOGGER.info("registering channel: " + this);

    if (nonNull(this.eventLoop)) {
      throw new IllegalStateException("channel has already registered to an event loop");
    }

    this.eventLoop = requireNonNull(eventLoop, "eventLoop");

    final var future = new CompletableFuture<Void>();

    eventLoop().execute(() -> register(future));

    return future;
  }

  @Override
  public CompletableFuture<Void> deregister() {
    LOGGER.info("de-registering channel: " + this);

    if (isNull(this.eventLoop)) {
      throw new IllegalStateException("channel has not yet registered to an event loop");
    }

    final var future = new CompletableFuture<Void>();

    try {
      selectionKey.cancel();

      future.complete(null);

      this.eventLoop = null;
    } catch (Exception e) {
      future.completeExceptionally(e);
    }

    return future;
  }

  private void register(final CompletableFuture<Void> future) {
    LOGGER.info("do register channel");

    try {
      selectionKey = javaChannel().register(eventLoop().selector(), 0, this);

      future.complete(null);

      pipeline().channelRegistered();

      unsafe.beginRead();
    } catch (Throwable t) {
      future.completeExceptionally(t);
    }
  }

  @Override
  public EventLoop eventLoop() {
    if (isNull(eventLoop)) {
      throw new IllegalStateException("channel is not registered yet");
    }
    return eventLoop;
  }
}
