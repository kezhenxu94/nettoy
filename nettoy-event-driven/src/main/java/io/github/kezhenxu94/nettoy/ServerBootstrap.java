package io.github.kezhenxu94.nettoy;

import io.github.kezhenxu94.nettoy.channel.DefaultEventLoop;
import io.github.kezhenxu94.nettoy.channel.ServerNioChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @author kezhenxu94
 */
@Log
@RequiredArgsConstructor
public class ServerBootstrap {
  private final ServerBootstrapConfig config;

  public Future<Throwable> bind(final int port) throws Exception {
    final var eventLoop = new DefaultEventLoop(Selector.open());
    final var serverChannel = new ServerNioChannel();

    serverChannel.pipeline().addHandler(new ServerAcceptor(config.childHandler()));

    return serverChannel.register(eventLoop).thenCompose(throwable -> {
      if (throwable != null) { // failed to register
        return CompletableFuture.completedFuture(throwable);
      }

      return serverChannel.bind(new InetSocketAddress(port));
    });
  }

}
