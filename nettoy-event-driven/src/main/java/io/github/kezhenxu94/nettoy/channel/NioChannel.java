package io.github.kezhenxu94.nettoy.channel;

import lombok.extern.java.Log;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;

/**
 * @author kezhenxu94
 */
@Log
public class NioChannel extends AbstractNioChannel {
  public NioChannel(final SelectableChannel javaChannel) throws IOException {
    super(javaChannel);
  }

  @Override
  public CompletableFuture<Throwable> bind(final SocketAddress localAddress) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void read() throws IOException {
    final SocketChannel channel = (SocketChannel) javaChannel();
    final ByteBuffer buffer = ByteBuffer.allocate(1024);
    channel.read(buffer);

    pipeline().channelRead(buffer);
  }

  @Override
  public void beginRead() {
    eventLoop().execute(() -> selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_READ));
  }
}
