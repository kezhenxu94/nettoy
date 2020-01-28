package io.github.kezhenxu94.nettoy;

import io.github.kezhenxu94.nettoy.channel.Channel;
import io.github.kezhenxu94.nettoy.channel.ChannelHandler;
import lombok.extern.java.Log;

/**
 * @author kezhenxu94
 */
@Log
public class EchoHandler implements ChannelHandler {
  @Override
  public void channelRegistered(final Channel channel) {
    LOGGER.info("channel registered: " + channel);
  }

  @Override
  public void channelRead(final Channel channel, final Object msg) {
    LOGGER.info("message read: " + msg);
  }
}
