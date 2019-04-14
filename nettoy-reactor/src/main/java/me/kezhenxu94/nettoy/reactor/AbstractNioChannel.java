package me.kezhenxu94.nettoy.reactor;

import java.io.IOException;
import java.nio.channels.SelectableChannel;

/**
 * Created by kezhenxu94 in 2019-02-03 23:16
 *
 * @author kezhenxu94 (kezhenxu94 at 163 dot com)
 */
public abstract class AbstractNioChannel {
  protected final SelectableChannel channel;
  protected final int interestedOps;
  protected final ChannelHandler channelHandler;
  private NioEventLoop eventLoop;

  protected AbstractNioChannel(
      final SelectableChannel channel,
      final ChannelHandler channelHandler,
      final int interestedOps) {
    this.channel = channel;
    this.channelHandler = channelHandler;
    this.interestedOps = interestedOps;
    try {
      this.channel.configureBlocking(false);
    } catch (IOException e) {
      throw new RuntimeException("Failed to set blocking mode: " + this.channel);
    }
  }

  public NioEventLoop eventLoop() {
    return eventLoop;
  }

  public ChannelHandler handler() {
    return channelHandler;
  }

  public void register(final NioEventLoop eventLoop) throws Exception {
    this.eventLoop = eventLoop;
    this.channel.register(eventLoop.selector(), interestedOps, this);
  }

  public SelectableChannel javaChannel() {
    return channel;
  }

  public void write(final Object msg) {
  }
}
