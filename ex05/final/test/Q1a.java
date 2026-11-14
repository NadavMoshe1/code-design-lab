package test;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;

/**
 * Starts every submitted Callable on its own new thread.
 * The FutureTask supplies the standard Future completion/error/cancellation behavior.
 */
public class Q1a {

    private final Object lock = new Object();
    private final Set<Thread> runningThreads = new HashSet<>();
    private boolean closed = false;

    public Q1a() {
        // No thread is needed until threadIt is called.
    }

    public <V> Future<V> threadIt(Callable<V> f) {
        Objects.requireNonNull(f, "f");
        FutureTask<V> future = new FutureTask<>(f);

        Thread thread = new Thread(() -> {
            try {
                future.run();
            } finally {
                synchronized (lock) {
                    runningThreads.remove(Thread.currentThread());
                    lock.notifyAll();
                }
            }
        }, "threadIt-worker");

        synchronized (lock) {
            if (closed) {
                throw new RejectedExecutionException("Q1a is closed");
            }
            runningThreads.add(thread);
            try {
                thread.start();
            } catch (RuntimeException | Error e) {
                runningThreads.remove(thread);
                lock.notifyAll();
                throw e;
            }
        }
        return future;
    }

    /**
     * Prevents new submissions and waits for already-started threads.
     * If called from one of its own threads, it cannot wait for itself.
     */
    public void close() {
        synchronized (lock) {
            closed = true;
            if (runningThreads.contains(Thread.currentThread())) {
                return; // Avoid self-deadlock when called from a submitted Callable.
            }
            boolean interrupted = false;
            while (!runningThreads.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
