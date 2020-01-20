package io.github.kezhenxu94.nettoy.channel;

import java.nio.channels.SelectableChannel;

/**
 * @author kezhenxu94
 */
public interface Channel {
  SelectableChannel javaChannel();
}
