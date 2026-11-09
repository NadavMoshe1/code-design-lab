
import java.util.*;
import java.util.concurrent.*;

@SuppressWarnings("deprecation")
public final class ObservableFuture<V> extends Observable {

    private final Object lock = new Object();
    private final Set<Observer> registered = new HashSet<>();

    private V result = null;
    private boolean completed = false;

    // Constructor
    public ObservableFuture(Future<V> future) {
        Objects.requireNonNull(future);

        Thread worker = new Thread(() -> {
            try {
                // Wait for the Future to complete
                V value = future.get();

                synchronized (lock) {
                    result = value;
                    completed = true;
                    setChanged();
                }

                // Notify observers outside the lock
                notifyObservers(value);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

            } catch (ExecutionException | CancellationException e) {
                // Future failed or was cancelled
            }
        });

        worker.setDaemon(true);
        worker.start();
    }

    // Return the result without waiting
    public V get() {
        synchronized (lock) {
            return completed ? result : null;
        }
    }

    // Register a new observer
    @Override
    public void addObserver(Observer observer) {
        Objects.requireNonNull(observer);

        V value;

        synchronized (lock) {
            // Prevent duplicate registration
            if (!registered.add(observer)) {
                return;
            }

            if (!completed) {
                super.addObserver(observer);
                return;
            }

            value = result;
        }

        // Future already completed
        observer.update(this, value);
    }

    // Remove an observer
    @Override
    public void deleteObserver(Observer observer) {
        synchronized (lock) {
            registered.remove(observer);
            super.deleteObserver(observer);
        }
    }

    // Remove all observers
    @Override
    public void deleteObservers() {
        synchronized (lock) {
            registered.clear();
            super.deleteObservers();
        }
    }

    // Return number of registered observers
    @Override
    public int countObservers() {
        synchronized (lock) {
            return registered.size();
        }
    }
}
