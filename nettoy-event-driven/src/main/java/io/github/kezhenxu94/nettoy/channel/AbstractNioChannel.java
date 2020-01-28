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

  protected EventLoop eventLoop;
  protected SelectionKey selectionKey;

  public AbstractNioChannel(final SelectableChannel javaChannel) throws IOException {
    this.javaChannel = javaChannel;
    this.javaChannel.configureBlocking(false);
    this.pipeline = new DefaultPipeline(this);
  }

  @Override
  public CompletableFuture<Throwable> register(final EventLoop eventLoop) {
    LOGGER.info("registering channel: " + this);

    if (nonNull(this.eventLoop)) {
      throw new IllegalStateException("channel has already registered to an event loop");
    }

    this.eventLoop = requireNonNull(eventLoop, "eventLoop");

    final CompletableFuture<Throwable> future = new CompletableFuture<>();

    eventLoop().execute(() -> register(future));

    return future;
  }

  private void register(final CompletableFuture<Throwable> future) {
    LOGGER.info("do register channel");

    try {
      selectionKey = javaChannel().register(eventLoop().selector(), 0, this);

      future.complete(null);

      pipeline().channelRegistered();

      beginRead();
    } catch (Throwable t) {
      future.complete(t);
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
