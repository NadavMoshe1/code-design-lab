
import java.util.Observable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("deprecation")
public class ObservableFuture<V> extends Observable {

    private final Future<V> future;
    private volatile V value = null;

    public ObservableFuture(Future<V> future) {
        this.future = future;

        Thread thread = new Thread(() -> {
            try {
                // Wait for the Future in a background thread
                V result = future.get();

                // Save the result
                value = result;

                // Notify all observers
                setChanged();
                notifyObservers();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }

    public V get() {
        return value;
    }
}
