package me.kezhenxu94.nettoy.bio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Created by kezhenxu in 2018/8/28 21:53
 *
 * @author kezhenxu (kezhenxu94 at 163 dot com)
 */
public class EchoServer {
  private static final Logger LOGGER = Logger.getLogger(EchoServer.class.getSimpleName());
  private static final String POISON_PILL = "BYE";

  private Executor executor = Executors.newCachedThreadPool();

  public void start() throws IOException {
    final ServerSocket serverSocket = new ServerSocket();
    serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), 8080));
    while (true) {
      final Socket socket = serverSocket.accept();
      executor.execute(new SocketHandler(socket));
    }
  }

  public static void main(String[] args) throws IOException {
    new EchoServer().start();
  }

  private class SocketHandler implements Runnable {
    private final Socket socket;

    SocketHandler(Socket socket) {
      this.socket = socket;
    }

    @Override
    public void run() {
      try (final Socket ignored = socket;
           final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
           final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
          LOGGER.info("<=== " + line);
          writer.write(line);
          writer.newLine();
          writer.flush();
          LOGGER.info("===> " + line);
          if (line.equals(POISON_PILL)) {
            break;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        LOGGER.info("closing socket " + socket + ": " + socket.isClosed());
      }
    }
  }
}
