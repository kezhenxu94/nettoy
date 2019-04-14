package me.kezhenxu94.nettoy.reactor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

/**
 * Created by kezhenxu94 in 2019-02-04 10:44
 *
 * @author kezhenxu94 (kezhenxu94 at 163 dot com)
 */
public class NioServerChannel extends AbstractNioChannel {
  protected NioServerChannel(final ChannelHandler channelHandler) throws IOException {
    super(ServerSocketChannel.open(), channelHandler, SelectionKey.OP_ACCEPT);
  }

  public void bind(final int port) throws Exception {
    ((ServerSocketChannel) javaChannel()).bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
  }
}
