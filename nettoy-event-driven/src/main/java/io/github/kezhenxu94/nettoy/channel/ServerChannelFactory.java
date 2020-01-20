package io.github.kezhenxu94.nettoy.channel;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

/**
 * @author kezhenxu94
 */
public final class ServerChannelFactory {
  public Channel newInstance() throws IOException {
    final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    return new ServerChannel(serverSocketChannel);
  }
}
