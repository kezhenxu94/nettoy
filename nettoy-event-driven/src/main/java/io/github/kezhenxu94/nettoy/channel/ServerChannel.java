package io.github.kezhenxu94.nettoy.channel;

import java.nio.channels.SelectableChannel;

/**
 * @author kezhenxu94
 */
public class ServerChannel implements Channel {
  private final SelectableChannel javaChannel;

  public ServerChannel(final SelectableChannel javaChannel) {
    this.javaChannel = javaChannel;
  }

  @Override
  public SelectableChannel javaChannel() {
    return javaChannel;
  }
}
