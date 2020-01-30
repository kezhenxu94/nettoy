package io.github.kezhenxu94.nettoy.channel;

import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation of {@link Pipeline}.
 *
 * @author kezhenxu94
 */
public class DefaultPipeline implements Pipeline {
  private final List<ChannelHandler> handlers;

  @Getter
  private final Channel channel;

  public DefaultPipeline(final Channel channel) {
    this.channel = requireNonNull(channel, "channel");
    this.handlers = new CopyOnWriteArrayList<>();
  }

  @Override
  public Pipeline addHandler(final ChannelHandler handler) {
    handlers.add(handler);
    return this;
  }

  @Override
  public void channelRegistered() {
    handlers.forEach(h -> h.channelRegistered(channel()));
  }

  @Override
  public void channelRead(final Object msg) {
    handlers.forEach(h -> h.channelRead(channel(), msg));
  }

}
