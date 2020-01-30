package io.github.kezhenxu94.nettoy;

import lombok.extern.java.Log;

/**
 * @author kezhenxu94
 */
@Log
public class EchoServer {
  static {
    System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-5s] %5$s %n");
  }

  public static void main(String[] args) throws Exception {
    final var port = 8080;
    final var config = ServerBootstrapConfig.builder().childHandler(new EchoHandler()).build();
    final var bindFuture = new ServerBootstrap(config).bind(port);

    bindFuture.thenRun(() -> LOGGER.info("started successfully"))
              .exceptionally(throwable -> handleException(port, throwable));
  }

  private static Void handleException(final int port, final Throwable throwable) {
    LOGGER.severe("failed to bind to port: " + port);
    throwable.printStackTrace();
    return null;
  }
}
