package test;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Active Object: a single dedicated thread executes accepted Runnable tasks in FIFO order.
 */
public class Q1b {

    private static final Runnable STOP = () -> { };

    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final Object lock = new Object();
    private final Thread worker;
    private boolean accepting = true;

    public Q1b() {
        worker = new Thread(this::processTasks, "active-object-worker");
        worker.start();
    }

    public void push(Runnable r) {
        Objects.requireNonNull(r, "r");
        synchronized (lock) {
            if (!accepting) {
                // The provided MainTrain1b calls push() after close() without catching
                // an exception; rejection is therefore a no-op (never queued or run).
                return;
            }
            queue.add(r);
        }
    }

    private void processTasks() {
        while (true) {
            Runnable task;
            try {
                task = queue.take(); // Blocks without busy waiting.
            } catch (InterruptedException e) {
                continue; // Interruption does not discard accepted tasks.
            }

            if (task == STOP) {
                return;
            }
            try {
                task.run();
            } catch (Throwable failure) {
                // A failed task must not prevent subsequent accepted tasks from running.
                failure.printStackTrace();
            }
        }
    }

    public void close() {
        synchronized (lock) {
            if (accepting) {
                accepting = false;
                // FIFO sentinel is behind every previously accepted task.
                queue.add(STOP);
            }
        }

        if (Thread.currentThread() == worker) {
            return; // Do not join the worker from itself.
        }

        boolean interrupted = false;
        while (true) {
            try {
                worker.join();
                break;
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }
}
