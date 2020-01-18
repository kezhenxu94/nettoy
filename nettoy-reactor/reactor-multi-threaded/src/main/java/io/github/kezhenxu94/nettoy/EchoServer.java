package io.github.kezhenxu94.nettoy;

import io.github.kezhenxu94.nettoy.reactor.Reactor;

/**
 * Created by kezhenxu94 in 2019-02-04 10:42
 *
 * @author kezhenxu94 (kezhenxu94 at 163 dot com)
 */
public final class EchoServer {
  public static void main(String[] args) throws Exception {
    new Reactor().bind(8080);
  }
}
