package io.github.kezhenxu94.nettoy;

import io.github.kezhenxu94.nettoy.channel.Channel;
import io.github.kezhenxu94.nettoy.channel.EventLoop;

import java.io.IOException;
import java.nio.channels.Selector;

/**
 * @author kezhenxu94
 */
public class SingleThreadEventLoop implements EventLoop {
  private final Selector selector;

  public SingleThreadEventLoop() throws IOException {
    selector = Selector.open();
  }

  @Override
  public void register(final Channel channel) throws Exception {
    channel.javaChannel().register(selector, 0, this);
  }

  @Override
  public void execute() {

  }
}
