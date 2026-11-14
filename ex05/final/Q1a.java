import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Q1a {
    /**
     * Runs f on a new thread and immediately returns a Future for its result.
     * FutureTask implements get(), cancellation, and exception propagation.
     */
    public static <V> Future<V> threadIt(Callable<V> f) {
        Objects.requireNonNull(f, "f must not be null");

        FutureTask<V> future = new FutureTask<>(f);
        Thread thread = new Thread(future, "threadIt-worker");
        thread.start();
        return future;
    }
}
