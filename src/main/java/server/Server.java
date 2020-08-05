package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

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


  public static class CancellingExecutor extends ThreadPoolExecutor {

    private final Object lock = new Object();
    protected final List<RunnableFuture> submittedTasks = new ArrayList<>();

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
      super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public <T> Future<T> submit(Callable<T> task) {
      Future<T> future = super.submit(task);
      if (future instanceof RunnableFuture) {
        synchronized (lock) {
          submittedTasks.add((RunnableFuture) future);
        }
      }
      return future;
    }

    public void shutdown() {
      synchronized (lock) {
        for (RunnableFuture runnableFuture : submittedTasks) {
          runnableFuture.cancel(true);
        }
      }
      super.shutdown();
    }

    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
      if (callable instanceof CancellableTask)
        return ((CancellableTask<T>) callable).newTask();
      else
        return super.newTaskFor(callable);
    }


  }

  public static interface CancellableTask<T> extends Callable<T> {

    void cancel();

    RunnableFuture<T> newTask();

  }
}
