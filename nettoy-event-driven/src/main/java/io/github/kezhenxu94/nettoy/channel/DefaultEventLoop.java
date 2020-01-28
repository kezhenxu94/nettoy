package io.github.kezhenxu94.nettoy.channel;

import lombok.Getter;
import lombok.extern.java.Log;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.Objects.nonNull;

/**
 * Default implementation of {@link EventLoop}
 *
 * @author kezhenxu94
 */
@Log
public class DefaultEventLoop implements EventLoop, Runnable {
  @Getter
  private final Selector selector;
  private final Executor executor;
  private final BlockingQueue<Runnable> taskQueue;

  private volatile boolean started = false;

  public DefaultEventLoop(final Selector selector) {
    LOGGER.info("creating event loop: " + getClass().getName());
    this.selector = selector;
    this.executor = Executors.newSingleThreadExecutor();
    this.taskQueue = new LinkedBlockingQueue<>();
  }

  private void startThread() {
    LOGGER.info("starting event loop thread");
    executor.execute(this);
  }

  @Override
  public void execute(final Runnable task) {
    if (!taskQueue.offer(task)) {
      throw new RuntimeException("failed to add task");
    }
    if (!started) {
      startThread();
    }
  }

  @Override
  public void run() {
    LOGGER.info("event loop started");

    // noinspection InfiniteLoopStatement
    while (true) {
      started = true;
      try {
        if (selector.select(300L) == 0) {
          runPendingTasks();
          continue;
        }

        final Set<SelectionKey> selectionKeys = selector.selectedKeys();
        handleSelectedKeys(selectionKeys);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void handleSelectedKeys(final Set<SelectionKey> keys) throws IOException {
    for (final Iterator<SelectionKey> it = keys.iterator(); it.hasNext(); it.remove()) {
      final SelectionKey key = it.next();
      final Channel channel = (Channel) key.attachment();
      if (!key.isValid()) {
        channel.javaChannel().close();
        continue;
      }

      if (key.isAcceptable() || key.isReadable()) {
        read(channel);
      }

      if (key.isWritable()) {
        write(channel);
      }
    }
  }

  private void write(final Channel channel) {
    LOGGER.info("writing to channel: " + channel);
  }

  private void read(final Channel channel) throws IOException {
    LOGGER.info("reading from channel: " + channel);
    channel.read();
  }

  private void runPendingTasks() {
    if (taskQueue.isEmpty()) {
      return;
    }

    LOGGER.info("running " + taskQueue.size() + " pending tasks");

    final List<Runnable> tasks = new ArrayList<>();
    taskQueue.drainTo(tasks);

    for (final Runnable task : tasks) {
      if (nonNull(task)) {
        task.run();
      }
    }
  }
}
