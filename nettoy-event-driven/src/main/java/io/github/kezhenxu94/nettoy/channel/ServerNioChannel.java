package io.github.kezhenxu94.nettoy.channel;

import lombok.extern.java.Log;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.CompletableFuture;

/**
 * @author kezhenxu94
 */
@Log
public class ServerNioChannel extends AbstractNioChannel {

  public ServerNioChannel() throws IOException {
    super(ServerSocketChannel.open());
  }

  @Override
  public CompletableFuture<Throwable> bind(final SocketAddress localAddress) {
    final CompletableFuture<Throwable> future = new CompletableFuture<>();

    eventLoop().execute(() -> {
      try {
        ((ServerSocketChannel) javaChannel()).bind(localAddress);

        LOGGER.info("channel bound: " + localAddress);

        future.complete(null);
      } catch (IOException e) {
        future.complete(e);
      }
    });

    return future;
  }

  @Override
  public void read() {
    LOGGER.info("accepting from channel: " + this);

    try {
      final Channel childChannel = new NioChannel(((ServerSocketChannel) javaChannel()).accept());
      pipeline().channelRead(childChannel);
      childChannel.register(eventLoop());
    } catch (IOException e) {
      throw new RuntimeException("failed to accept new connection", e);
    }
  }

  @Override
  public void beginRead() {
    eventLoop().execute(() -> selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_ACCEPT));
  }
}
