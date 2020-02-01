package io.github.kezhenxu94.nettoy.bio;

import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by kezhenxu94 in 2018/8/28 21:58
 *
 * @author kezhenxu94 (kezhenxu94 at 163 dot com)
 */
@Log
public class EchoClient {
  private static final String POISON_PILL = "BYE";

  private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

  public void start() throws IOException, InterruptedException {
    final var socket = new Socket();
    socket.connect(new InetSocketAddress(8080));

    final Thread readerThread = new Thread(new ReaderTask());
    readerThread.setDaemon(true);
    readerThread.start();

    final var writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    final var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    for (var msg = queue.take(); !Thread.interrupted(); msg = queue.take()) {
      LOGGER.info("===> " + msg);

      writer.write(msg);
      writer.newLine();
      writer.flush();

      final var response = reader.readLine();
      LOGGER.info("<=== " + response);

      if (response.equals(POISON_PILL)) {
        break;
      }
    }
  }

  private class ReaderTask implements Runnable {
    @Override
    public void run() {
      try (final var userReader = new BufferedReader(new InputStreamReader(System.in))) {
        for (var line = userReader.readLine(); line != null; line = userReader.readLine()) {
          queue.put(line);
        }
      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    new EchoClient().start();
  }
}
