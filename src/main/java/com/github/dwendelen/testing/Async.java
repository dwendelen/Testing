package com.github.dwendelen.testing;

import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class Async {
    public static Thread runAsync(final Runnable runnable) {
        Thread thread = new Thread() {
            public void run() {
                runnable.run();
            }
        };
        thread.start();
        return thread;
    }

    public static TaskScheduler newDirectTaskScheduler() {
        return new DirectTaskScheduler();
    }
    
    public static AsyncTaskExecutor newDirectAsyncTaskExecutor() {
        return new DirectAsyncTaskExecutor();
    }

    public static ExecutorService newDirectExecutorService() {
        return new DirectExecutorService();
    }

    public static <T> ScheduledFuture<T> newImmediateScheduledFuture(T value) {
        return new ImmediateFuture<>(value);
    }
    
    public static <T> ScheduledFuture<T> newImmediateFailedScheduledFuture(Exception e) {
        return new ImmediateFailedFuture<T>(e);
    }

    public static ControllableExecutor newControllableExecutor() {
        return new ControllableExecutor();
    }

    private static class DirectAsyncTaskExecutor implements AsyncTaskExecutor {
        @Override
        public void execute(Runnable task, long startTimeout) {
            execute(task);
        }

        @Override
        public Future<?> submit(Runnable task) {
            try {
                task.run();
                return newImmediateScheduledFuture(null);
            } catch (Exception e) {
                return newImmediateFailedScheduledFuture(e);
            }
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            try {
                return newImmediateScheduledFuture(task.call());
            } catch (Exception e) {
                return newImmediateFailedScheduledFuture(e);
            }
        }

        @Override
        public void execute(Runnable task) {
            try {
                task.run();
            } catch (Exception e) {
                //Eaten, like real async executors
            }
        }
    }

    private static class DirectExecutorService implements ExecutorService {
        @Override
        public void shutdown() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Runnable> shutdownNow() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            try {
                return newImmediateScheduledFuture(task.call());
            } catch (Exception e) {
                return newImmediateFailedScheduledFuture(e);
            }
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            try {
                task.run();
                return newImmediateScheduledFuture(result);
            } catch (Exception e) {
                return newImmediateFailedScheduledFuture(e);
            }
        }

        @Override
        public Future<?> submit(Runnable task) {
            try {
                task.run();
                return newImmediateScheduledFuture(null);
            } catch (Exception e) {
                return newImmediateFailedScheduledFuture(e);
            }
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void execute(Runnable command) {
            try {
                command.run();
            } catch (Exception e) {
                //Eat exception like a async executor
            }
        }
    }
    
    private static class DirectTaskScheduler implements TaskScheduler {
        private ScheduledFuture<?> run(Runnable task) {
            try {
                task.run();
                return newImmediateScheduledFuture(null);
            } catch (Exception e) {
                return newImmediateFailedScheduledFuture(e);
            }
        }

        @Override
        public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
            return run(task);
        }

        @Override
        public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
            return run(task);
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
            return run(task);
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
            return run(task);
        }

        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
            return run(task);
        }

        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
            return run(task);
        }
    }
    
    private static abstract class AbstractImmediateFuture<T> implements ScheduledFuture<T> 
    {
        @Override
        public long getDelay(TimeUnit unit) {
            return 0;
        }

        @Override
        public int compareTo(Delayed o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return get();
        }
    }
    
    private static class ImmediateFuture<T> extends AbstractImmediateFuture<T> {
        private T value;
        
        private ImmediateFuture(T value) {
            this.value = value;
        }
        
        @Override
        public T get() throws InterruptedException, ExecutionException {
            return value;
        }
    }

    private static class ImmediateFailedFuture<T> extends AbstractImmediateFuture<T> {
        private Exception exception;
        
        private ImmediateFailedFuture(Exception exception) {
            this.exception = exception;
        }
        
        @Override
        public T get() throws InterruptedException, ExecutionException {
            throw new ExecutionException(exception);
        }
    }
}
