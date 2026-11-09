
import java.util.*;
import java.util.concurrent.*;

@SuppressWarnings("deprecation")
public final class ObservableFuture<V> extends Observable {

    private final Object lock = new Object();

    private final Set<Observer> observers = new LinkedHashSet<>();
    private final Set<Observer> notified = new HashSet<>();

    private V value = null;
    private boolean completed = false;

    // Constructor
    public ObservableFuture(Future<V> future) {
        Objects.requireNonNull(future);

        Thread worker = new Thread(() -> {
            try {
                // Wait for the Future in a background thread
                V result = future.get();

                List<Observer> toNotify = new ArrayList<>();

                synchronized (lock) {
                    // Save the result
                    value = result;
                    completed = true;

                    // Prepare observers for notification
                    for (Observer observer : observers) {
                        if (notified.add(observer)) {
                            toNotify.add(observer);
                        }
                    }
                }

                // Notify outside the lock
                for (Observer observer : toNotify) {
                    notifySafely(observer, result);
                }

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
            return completed ? value : null;
        }
    }

    // Register an observer
    @Override
    public void addObserver(Observer observer) {
        Objects.requireNonNull(observer);

        V result;

        synchronized (lock) {
            observers.add(observer);

            if (!completed || !notified.add(observer)) {
                return;
            }

            result = value;
        }

        // Notify if the Future already completed
        notifySafely(observer, result);
    }

    // Remove an observer
    @Override
    public void deleteObserver(Observer observer) {
        synchronized (lock) {
            observers.remove(observer);
        }
    }

    // Remove all observers
    @Override
    public void deleteObservers() {
        synchronized (lock) {
            observers.clear();
        }
    }

    // Return the number of registered observers
    @Override
    public int countObservers() {
        synchronized (lock) {
            return observers.size();
        }
    }

    // Notify an observer without affecting others
    private void notifySafely(Observer observer, V result) {
        try {
            observer.update(this, result);
        } catch (RuntimeException e) {
            System.err.println("Observer notification failed: " + e);
        }
    }
}
