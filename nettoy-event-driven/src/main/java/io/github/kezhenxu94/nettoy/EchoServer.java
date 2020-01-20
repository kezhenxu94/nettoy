package io.github.kezhenxu94.nettoy;

import io.github.kezhenxu94.nettoy.channel.Channel;
import io.github.kezhenxu94.nettoy.channel.EventLoop;
import io.github.kezhenxu94.nettoy.channel.ServerChannelFactory;

/**
 * @author kezhenxu94
 */
public class EchoServer {
  public static void main(String[] args) throws Exception {
    final EventLoop eventLoop = new SingleThreadEventLoop();
    final ServerChannelFactory channelFactory = new ServerChannelFactory();
    final Channel serverChannel = channelFactory.newInstance();

    eventLoop.register(serverChannel);
  }
}
