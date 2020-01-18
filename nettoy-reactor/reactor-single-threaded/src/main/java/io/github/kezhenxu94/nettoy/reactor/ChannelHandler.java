package io.github.kezhenxu94.nettoy.reactor;

/**
 * @author kezhenxu94
 */
public interface ChannelHandler {
  void read() throws Exception;

  void write() throws Exception;
}
