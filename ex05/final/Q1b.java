import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;

/**
 * An Active Object that executes accepted tasks sequentially on one worker.
 */
public class Q1b implements AutoCloseable {
    private final Object lock = new Object();
    private final Deque<Runnable> queue = new ArrayDeque<>();
    private final Thread worker;
    private boolean accepting = true; // accessed only while holding lock

    public Q1b() {
        worker = new Thread(this::runTasks, "active-object-worker");
        worker.start();
    }

    /** Adds a task unless shutdown has already started. */
    public void push(Runnable task) {
        Objects.requireNonNull(task, "task must not be null");
        synchronized (lock) {
            if (!accepting) {
                throw new RejectedExecutionException("Active Object is closing or closed");
            }
            queue.addLast(task);
            lock.notifyAll();
        }
    }

    private void runTasks() {
        while (true) {
            Runnable task;
            synchronized (lock) {
                while (queue.isEmpty() && accepting) {
                    try {
                        lock.wait(); // sleep until a task arrives or close() is called
                    } catch (InterruptedException e) {
                        // Interruption alone must not discard accepted tasks.
                    }
                }
                if (queue.isEmpty()) {
                    return; // closed and all accepted tasks have finished
                }
                task = queue.removeFirst();
            }

            try {
                task.run();
            } catch (Throwable failure) {
                // One failing task must not terminate the worker.
                failure.printStackTrace();
            }
        }
    }

    /**
     * Rejects new work and waits for all previously accepted tasks to finish.
     * Can be called more than once. Calling it from the worker itself only
     * requests shutdown, to avoid waiting for the current thread to exit.
     */
    @Override
    public void close() {
        synchronized (lock) {
            accepting = false;
            lock.notifyAll();
        }

        if (Thread.currentThread() == worker) {
            return; // avoid self-join; remaining tasks will still be drained
        }

        boolean interrupted = false;
        try {
            while (true) {
                try {
                    worker.join();
                    return;
                } catch (InterruptedException e) {
                    interrupted = true; // still wait for orderly shutdown
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
