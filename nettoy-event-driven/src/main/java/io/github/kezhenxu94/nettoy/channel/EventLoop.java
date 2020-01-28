package io.github.kezhenxu94.nettoy.channel;

import java.nio.channels.Selector;
import java.util.concurrent.Executor;

/**
 * @author kezhenxu94
 */
public interface EventLoop extends Executor {
  Selector selector();
}
