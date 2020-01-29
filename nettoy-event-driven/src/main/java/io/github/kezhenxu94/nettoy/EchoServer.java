package io.github.kezhenxu94.nettoy;

import lombok.extern.java.Log;

import java.util.concurrent.Future;

/**
 * @author kezhenxu94
 */
@Log
public class EchoServer {
  static {
    System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-5s] %5$s %n");
  }

  public static void main(String[] args) throws Exception {
    final int port = 8080;
    final ServerBootstrapConfig config = ServerBootstrapConfig.builder()
                                                              .childHandler(new EchoHandler())
                                                              .build();
    final Future<Throwable> bindFuture = new ServerBootstrap(config).bind(port);

    final Throwable cause = bindFuture.get();

    if (cause == null) {
      LOGGER.info("started successfully");
    } else {
      LOGGER.severe("failed to bind to port: " + port);
      cause.printStackTrace();
    }
  }
}
