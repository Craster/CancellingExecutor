package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Server {

  private final CancellingExecutor serverExecutor = new CancellingExecutor(1, 1,
      0L, TimeUnit.MILLISECONDS,
      new LinkedBlockingQueue<>());

  private final CancellingExecutor socketsExecutor = new CancellingExecutor(4, 18,
      1, TimeUnit.MINUTES,
      new LinkedBlockingQueue<>());

  public void start() {

    try {
      ServerSocket socket = new ServerSocket(4004);
      ServerSocketUsingTask task = new HandlerTask(socket, socketsExecutor);
      serverExecutor.submit(task);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void stop() {
    serverExecutor.shutdown();
  }


}
