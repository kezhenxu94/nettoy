package me.kezhenxu94.nettoy.reactor;

/**
 * Created by kezhenxu94 in 2019-02-04 14:57
 *
 * @author kezhenxu94 (kezhenxu94 at 163 dot com)
 */
public interface ChannelHandler {
  void onChannelAcceptable(final AbstractNioChannel channel);

  void onChannelWritable(final AbstractNioChannel channel);

  void onChannelReadable(final AbstractNioChannel channel);
}
