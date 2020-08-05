package server;

import events.HeartbeatEvent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.RejectedExecutionException;

public class HandlerTask extends ServerSocketUsingTask {

  public HandlerTask(ServerSocket socket, Server.CancellingExecutor executor) {
    super(socket, executor);
  }

  @Override
  public Object call() throws Exception {

    while (!executor.isShutdown()) {
      try {
        final Socket conn = socket.accept();
        SocketUsingTask task = new SocketUsingTask<Object>(conn) {
          @Override
          public Object call() throws Exception {
            handleRequest(conn);
            return null;
          }
        };
        executor.submit(task);

      } catch (RejectedExecutionException e) {
        if (!executor.isShutdown()) {
          System.out.println("Task is interrupted");
        }
      }
    }
    return null;
  }

  private void handleRequest(Socket socket) {

    try {
      try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
           ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
        Object request = objectInputStream.readObject();
        if (request instanceof HeartbeatEvent) {
          System.out.println(((HeartbeatEvent) request).getUserId());
        }
        out.write("OK");
        out.flush();
      }
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }

  }

}
