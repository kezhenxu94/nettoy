package io.github.kezhenxu94.nettoy;

import io.github.kezhenxu94.nettoy.channel.Channel;
import io.github.kezhenxu94.nettoy.channel.ChannelHandler;
import lombok.extern.java.Log;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author kezhenxu94
 */
@Log
public class EchoHandler implements ChannelHandler {
  private static final String POISON_PILL = "BYE";

  @Override
  public void channelRegistered(final Channel channel) {
    LOGGER.info("channel registered: " + channel);
  }

  @Override
  public void channelRead(final Channel channel, final Object msg) {
    LOGGER.info("message read: " + msg);

    final var buffer = (ByteBuffer) msg;
    buffer.flip();

    final var s = new String(buffer.array(), StandardCharsets.UTF_8);

    channel.write(buffer).thenRun(() -> {
      if (POISON_PILL.equals(s.trim())) {
        channel.close().exceptionally(t -> {
          t.printStackTrace();
          return null;
        });
      }
    }).exceptionally(throwable -> {
      throwable.printStackTrace();
      return null;
    });
  }
}
