package server;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public abstract class SocketUsingTask<T> implements Server.CancellableTask<T> {
  private Socket socket;
  private static final long WAIT_TIME = 60000;

  public SocketUsingTask(Socket socket) {
    this.socket = socket;
  }

  protected synchronized void setSocket(Socket socket) {
    this.socket = socket;
  }

  public synchronized void cancel() {
    try {
      if (socket != null) {
        socket.close();
      }
    } catch (IOException ignored) {
    }
  }

  public RunnableFuture<T> newTask() {
    return new FutureTask<T>(this) {
      public boolean cancel(boolean mayInterruptIfRunning) {
        try {
          SocketUsingTask.this.cancel();
        } finally {
          return super.cancel(mayInterruptIfRunning);
        }
      }
    };
  }


}
