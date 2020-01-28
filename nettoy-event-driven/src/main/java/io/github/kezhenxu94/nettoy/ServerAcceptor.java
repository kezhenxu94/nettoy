package io.github.kezhenxu94.nettoy;

import io.github.kezhenxu94.nettoy.channel.Channel;
import io.github.kezhenxu94.nettoy.channel.ChannelHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

/**
 * @author kezhenxu94
 */
@Log
@RequiredArgsConstructor
public class ServerAcceptor implements ChannelHandler {
  private final ChannelHandler childHandler;

  @Override
  public void channelRegistered(final Channel channel) {
  }

  @Override
  public void channelRead(final Channel channel, final Object msg) {
    LOGGER.info("channel read: " + msg);

    ((Channel) msg).pipeline().addHandler(childHandler);
  }
}
