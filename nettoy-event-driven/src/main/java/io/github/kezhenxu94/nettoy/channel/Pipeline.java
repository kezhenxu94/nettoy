package io.github.kezhenxu94.nettoy.channel;

/**
 * @author kezhenxu94
 */
public interface Pipeline {
  Channel channel();

  Pipeline addHandler(final ChannelHandler handler);

  void channelRegistered();

  void channelRead(final Object msg);
}
