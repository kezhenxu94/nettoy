package me.kezhenxu94.nettoy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Created by kezhenxu94 in 2018/8/28 21:58
 *
 * @author kezhenxu94 (kezhenxu94 at 163 dot com)
 */
public class EchoClient {
  private static final Logger LOGGER = Logger.getLogger(EchoClient.class.getSimpleName());
  private static final String POISON_PILL = "BYE";

  public void start() throws IOException {
    try (final Socket socket = new Socket(InetAddress.getLocalHost(), 8080);
         final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
         final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
      new Thread(new ReaderTask(writer)).start();
      for (String line = reader.readLine(); line != null && !line.equals(POISON_PILL); line = reader.readLine()) {
        LOGGER.info("<=== " + line);
      }
    }
  }

  private static class ReaderTask implements Runnable {
    private final BufferedWriter writer;

    ReaderTask(final BufferedWriter writer) {
      this.writer = writer;
    }

    @Override
    public void run() {
      try (BufferedReader userReader = new BufferedReader(new InputStreamReader(System.in))) {
        for (String line = userReader.readLine(); line != null; line = userReader.readLine()) {
          LOGGER.info("===> " + line);
          writer.write(line);
          writer.newLine();
          writer.flush();
          if (line.equals(POISON_PILL)) {
            break;
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) throws IOException {
    new EchoClient().start();
  }
}