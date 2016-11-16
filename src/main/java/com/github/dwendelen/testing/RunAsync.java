package com.github.dwendelen.testing;

public class RunAsync {
    public static Thread runAsync(final Runnable runnable) {
        Thread thread = new Thread() {
            public void run() {
                runnable.run();
            }
        };
        thread.start();
        return thread;
    }
}
