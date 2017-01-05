package com.github.dwendelen.testing;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;

public class ControllableExecutor implements Executor {
    private Queue<Runnable> queue = new LinkedList<>();

    @Override
    public void execute(Runnable command) {
        queue.add(command);
    }

    public void runNextTask() {
        Runnable runnable = queue.poll();
        if(runnable == null) {
            throw new RuntimeException("No task to run");
        }
        runnable.run();
    }

    public void runAllTask() {
        for (Runnable runnable : queue) {
            runnable.run();
        }
    }

    public int getNbOfPendingTasks() {
        return queue.size();
    }
}
