# failure modes ai

Items 1–5 restate my own list in `failure-modes-human.md`. Items 6–19 are the additional failure modes the AI identified.

1. **Running the function on the calling thread:** `threadIt` calls `f` directly instead of starting a new thread. The caller blocks until `f` finishes, so the work is not asynchronous.

2. **Returning a result too early:** The `Future` gives back `null` or an unfinished value rather than making `get()` wait until the computation is complete.

3. **Running Active Object tasks in parallel:** Each `push()` starts a new thread, so tasks may run at the same time instead of sequentially on the same worker thread.

4. **Losing queued tasks during `close()`:** The worker stops immediately and does not finish tasks that were accepted before closing.

5. **Accepting tasks after closing:** `push()` still accepts new tasks after `close()` has closed the Active Object.

6. **Losing tasks because of a race condition:** `push()` and `close()` access the queue and closed flag without proper synchronization. A task may be accepted just as the worker exits, leaving it unexecuted.

7. **Worker exits when the queue is temporarily empty:** The worker checks the queue once and terminates when it is empty, even though future `push()` calls should still be processed before `close()`.

8. **Worker waits forever for a task:** The worker uses `wait()` or another blocking operation, but `push()` or `close()` does not wake it up correctly. Queued tasks may never run, or `close()` may never finish.

9. **Busy-waiting on an empty queue:** Instead of blocking while there is no work, the worker continuously checks the queue and wastes CPU time.

10. **A task exception kills the worker:** A `Runnable` throws an unchecked exception. If the worker does not handle it, later queued tasks never execute.

11. **A function exception is lost:** `f` throws an exception in the thread created by `threadIt`, but the `Future` never records the failure. A call to `get()` may block forever or report an incorrect result rather than completing exceptionally.

12. **Incorrect `Future` completion state:** The implementation reports `isDone()` as true before the computation finishes, or leaves it false after completion. This breaks the expected `Future` behavior.

13. **Unsafe visibility of the result:** The worker writes the result or completion flag without proper synchronization. Another thread calling `get()` or `isDone()` might not see the updated value.

14. **Executing a task more than once:** Incorrect queue handling allows two iterations or threads to remove or run the same task twice, even though each accepted task should run once.

15. **Holding the queue lock while running a task:** The worker executes `Runnable.run()` while holding a shared lock. Other threads cannot call `push()` or `close()` promptly, and a task that calls the Active Object itself may deadlock.

16. **`close()` returns before the queued work finishes:** The implementation marks the object as closed but does not wait for the worker to complete the already accepted tasks, even when `close()` is intended to perform an orderly shutdown.

17. **Repeated `close()` calls cause problems:** Calling `close()` twice throws an unexpected error, waits forever, or interferes with task completion because the implementation does not handle repeated shutdown requests safely.

18. **Interrupts are handled incorrectly:** An interrupted wait or worker thread causes tasks to be skipped, the worker to exit too early, or a waiting caller to block indefinitely.

19. **Using an executor or thread pool:** Even if the code appears to work, relying on `ExecutorService` or a thread pool in the Active Object violates the explicit restriction in the assignment.
