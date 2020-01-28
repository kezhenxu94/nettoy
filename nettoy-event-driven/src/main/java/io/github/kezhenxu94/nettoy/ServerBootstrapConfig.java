package io.github.kezhenxu94.nettoy;

import io.github.kezhenxu94.nettoy.channel.ChannelHandler;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author kezhenxu94
 */
@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class ServerBootstrapConfig {
  private final ChannelHandler childHandler;
}
