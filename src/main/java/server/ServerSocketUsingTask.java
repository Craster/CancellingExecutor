package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

abstract class ServerSocketUsingTask<T> implements CancellableTask<T> {
  final ServerSocket socket;
  final CancellingExecutor executor;

  ServerSocketUsingTask(ServerSocket socket, CancellingExecutor executor) {
    this.socket = socket;
    this.executor = executor;
  }

  public synchronized void cancel() {
    try {
      if (executor != null) {
        executor.shutdown();
      }
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
          ServerSocketUsingTask.this.cancel();
        } finally {
          return super.cancel(mayInterruptIfRunning);
        }
      }
    };
  }


}
