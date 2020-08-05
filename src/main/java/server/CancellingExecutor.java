package server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class CancellingExecutor extends ThreadPoolExecutor {

  private final Object lock = new Object();
  private final List<RunnableFuture> submittedTasks = new ArrayList<>();

  CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
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