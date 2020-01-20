package io.github.kezhenxu94.nettoy.channel;

/**
 * @author kezhenxu94
 */
public interface EventLoop {
  void register(final Channel channel) throws Exception;

  void execute();
}
