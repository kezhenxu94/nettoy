package io.github.kezhenxu94.nettoy.bio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * Created by kezhenxu94 in 2018/8/28 21:58
 *
 * @author kezhenxu94 (kezhenxu94 at 163 dot com)
 */
public class EchoClient {
  private static final Logger LOGGER = Logger.getLogger(EchoClient.class.getSimpleName());
  private static final String POISON_PILL = "BYE";

  private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

  public void start() throws IOException, InterruptedException {
    final Socket socket = new Socket();
    socket.connect(new InetSocketAddress(8080));

    final Thread readerThread = new Thread(new ReaderTask());
    readerThread.setDaemon(true);
    readerThread.start();

    final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    for (String msg = queue.take(); !Thread.interrupted(); msg = queue.take()) {
      LOGGER.info("===> " + msg);

      writer.write(msg);
      writer.newLine();
      writer.flush();

      final String response = reader.readLine();
      LOGGER.info("<=== " + response);

      if (response.equals(POISON_PILL)) {
        break;
      }
    }
  }

  private class ReaderTask implements Runnable {
    @Override
    public void run() {
      try (final BufferedReader userReader = new BufferedReader(new InputStreamReader(System.in))) {
        for (String line = userReader.readLine(); line != null; line = userReader.readLine()) {
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
