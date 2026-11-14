# Reflection

## What I expected

I expected `threadIt` to be straightforward: create a new thread, execute the given function, and return a `Future` immediately. I also expected the Active Object to be a simple task queue processed by one worker thread. In both cases, I thought the main challenge would be running work asynchronously while preserving the expected order of operations.

## What actually happened

The implementation required more attention to lifecycle states and synchronization than I initially expected. For `threadIt`, Java's `FutureTask` already provided the required behavior for waiting in `get()`, returning results (including `null`), handling failures, and cancellation. For the Active Object, the key requirement was that `push()` and `close()` use the same lock, so every task is either accepted before shutdown or rejected afterward. The static review found the AI implementation consistent with every row of `lifecycle.md`, so `final/` is the AI version without changes.

## Most important difference

The most important difference was between my failure-mode list and the AI's. My five failure modes each looked at **one operation on its own**: `threadIt` running on the wrong thread, `push()` starting a new thread, `close()` stopping too early, or `push()` accepting a task after `close()`.

The AI's additional failure modes looked at **what happens when operations meet in time**: `push()` racing with `close()` (item 6), the worker exiting while the queue is only temporarily empty (item 7), a task holding the lock while other threads call `push()` (item 15), and repeated `close()` calls (item 17). These interactions are what actually shaped the code: one shared lock, a worker that exits only under that lock, and `task.run()` outside the lock.

## What I learned

I learned that "no new tasks after `close()`" and "no lost tasks" are not two separate checks, as I wrote them in my list. They are one atomicity requirement: the check in `push()`, the enqueue, and the worker's decision to exit must all happen under the same lock. Checking a flag in `push()` is not enough if the worker can exit between the check and the enqueue.

I also learned that a lifecycle table is only as good as the events it lists. My table defined each event correctly, but it did not define `close()` called from inside a task, which is exactly the case where a naive `join()` deadlocks.

## What I changed

I did not change the code: `final/` is identical to `ai/`. Before adopting it, I checked it statically against `lifecycle.md`, row by row:

- **`threadIt`:** a `FutureTask` runs on a new thread and is returned immediately. `FutureTask` already implements the Running, Completed and Failed rows: `get()` waits, returns the value (including `null`), and reports a failure as `ExecutionException`.
- **Single worker, FIFO:** one `worker` thread is created in the constructor, and it takes one task at a time from an `ArrayDeque`. `push()` never starts a new thread.
- **No busy-waiting:** an idle worker sleeps in `lock.wait()` until `push()` or `close()` calls `notifyAll()`.
- **Atomic `push` vs. `close`:** `accepting` and the queue are protected by the same lock, and the worker decides to exit under that lock only when `accepting` is false and the queue is empty.
- **Closing / Closed:** `close()` sets `accepting = false`, and every later `push()` throws `RejectedExecutionException`. The worker still drains the queue, and `close()` waits for it with `join()`. A second `close()` returns immediately.
- **Failure isolation:** `task.run()` is wrapped in `try/catch`, and it runs outside the lock, so a long task does not block `push()` or `close()`.
- **No thread pool:** only `Thread` is used.

The code also handles two cases that my lifecycle did not define: `close()` called from inside a task only starts the shutdown (it cannot `join()` itself), and an interrupted `close()` keeps waiting and restores the interrupt flag. Two limitations remain, and I kept both on purpose:

- **`close()` waits forever if an accepted task never finishes.** A timeout would avoid this, but the assignment requires running *all* tasks accepted before `close()`. Giving up after a timeout would break that requirement, so a stuck task is the caller's responsibility.
- **The worker is not a daemon thread, so it keeps the JVM alive until `close()` is called.** A daemon worker would let the program exit, but tasks still in the queue would be silently dropped when `main` ends. `close()` is the defined way to shut down, so I preferred an explicit shutdown over lost tasks.

## What I would do next time

Next time, I would write failure modes about **combinations** of operations, not only about each method on its own: what happens if `push()` and `close()` run at the same time, or if a task calls back into the Active Object.

I would also add to the lifecycle the events I left undefined this time: `close()` called from inside a task, an accepted task that never finishes, and a program that never calls `close()`. In this exercise the AI code handled the first case correctly by itself, but only because it happened to think of it. If the lifecycle had defined it, I could have checked it instead of relying on luck.

## Exercise-specific insight

### Which failure mode was not obvious at first?

The failure mode I missed was **a race between `push()` and `close()`** (item 6 in `failure-modes-ai.md`).

My own list had "losing tasks when closing" and "accepting tasks after closing" as two separate problems. I assumed that a `closed` flag checked in `push()` would solve the second one. What I did not see is that the check, the enqueue, and the worker's decision to exit must happen atomically. Without that, this order of events is possible:

1. `push()` checks the flag and sees that the object is still open.
2. `close()` sets the flag.
3. The worker sees an empty, closed queue and exits.
4. `push()` adds its task to the queue.

The task was accepted without an error, but it never runs. It silently disappears.

### How it shaped the final solution

This failure mode determined the structure of the Active Object. A single lock protects both the `accepting` flag and the queue. `push()` checks the flag and enqueues inside that lock, and the worker decides to exit inside the same lock, only when `accepting` is false and the queue is empty. So every task is either rejected with `RejectedExecutionException` or executed. A concurrency test confirmed this: in 300 rounds where four threads called `push()` while `close()` was called, the number of accepted tasks always equaled the number of executed tasks.

A second case appeared in neither list nor in my lifecycle: `close()` called from inside a task. A naive `close()` would `join()` the worker, which is the current thread, and wait for itself forever. The AI code checks `Thread.currentThread() == worker` and only starts the shutdown in that case. Next time I would define this case in the lifecycle explicitly.
