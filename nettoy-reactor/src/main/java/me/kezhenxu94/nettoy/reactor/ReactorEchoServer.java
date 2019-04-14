package me.kezhenxu94.nettoy.reactor;

import java.io.IOException;

/**
 * Created by kezhenxu94 in 2019-02-04 10:42
 *
 * @author kezhenxu94 (kezhenxu94 at 163 dot com)
 */
@SuppressWarnings("WeakerAccess")
public class ReactorEchoServer {
  private final ChannelHandler handler = new ChannelHandler() {
    @Override
    public void onChannelAcceptable(final AbstractNioChannel channel) {
    }

    @Override
    public void onChannelWritable(final AbstractNioChannel channel) {
    }

    @Override
    public void onChannelReadable(final AbstractNioChannel channel) {
    }
  };
  private final NioEventLoop eventLoop;
  private final NioServerChannel serverChannel;

  public ReactorEchoServer() throws IOException {
    eventLoop = new NioEventLoop();
    serverChannel = new NioServerChannel(handler);
  }

  public void start() throws Exception {
    eventLoop.start();
    serverChannel.register(eventLoop);
    serverChannel.bind(8080);
  }

  public static void main(String[] args) throws Exception {
    new ReactorEchoServer().start();
  }
}
