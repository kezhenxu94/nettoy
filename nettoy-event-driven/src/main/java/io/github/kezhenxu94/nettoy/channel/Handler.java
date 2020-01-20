package io.github.kezhenxu94.nettoy.channel;

/**
 * @author kezhenxu94
 */
public interface Handler {
  void read() throws Exception;

  void write() throws Exception;
}
