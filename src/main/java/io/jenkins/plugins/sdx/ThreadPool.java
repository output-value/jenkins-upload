package io.jenkins.plugins.sdx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPool {
    private final static ThreadPool instance = new ThreadPool();

    public static ThreadPool getInstance() {
        return instance;
    }

    private ExecutorService threadPool = Executors.newCachedThreadPool();

    public Future<?> submit(Runnable runnable) {
        return threadPool.submit(runnable);
    }
}
