package io.github.kezhenxu94.nettoy.channel;

/**
 * Handler to handles events of a {@link Channel}.
 *
 * @author kezhenxu94
 */
public interface ChannelHandler {
  void channelRegistered(final Channel channel);

  void channelRead(final Channel channel, final Object msg);
}
