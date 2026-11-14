# Reflection

## What I expected

I expected `threadIt` to be straightforward: create a new thread, execute the given function, and return a `Future` immediately. I also expected the Active Object to be a simple task queue processed by one worker thread. In both cases, I thought the main challenge would be running work asynchronously while preserving the expected order of operations.

## What actually happened

The implementation required more attention to lifecycle states and synchronization than I initially expected. For `threadIt`, Java's `FutureTask` already provided the required behavior for waiting in `get()`, returning results (including `null`), handling failures, and cancellation. For the Active Object, the key requirement was that `push()` and `close()` use the same lock, so every task is either accepted before shutdown or rejected afterward.

The static review found the AI implementation consistent with every row of `lifecycle.md`. However, when I ran it against the provided `MainTrain1a` and `MainTrain1b`, it did not fit the given API: `Q1a.threadIt` was a static method with no `close()`, and `Q1b.push()` threw `RejectedExecutionException` after `close()`, which `MainTrain1b` does not catch. The AI never saw these test files, so it could not know. I therefore changed both classes in `final/`.

## Most important difference

The most important difference was between my failure-mode list and the AI's. My five failure modes each looked at **one operation on its own**: `threadIt` running on the wrong thread, `push()` starting a new thread, `close()` stopping too early, or `push()` accepting a task after `close()`.

The AI's additional failure modes looked at **what happens when operations meet in time**: `push()` racing with `close()` (item 6), the worker exiting while the queue is only temporarily empty (item 7), a task holding the lock while other threads call `push()` (item 15), and repeated `close()` calls (item 17). These interactions are what actually shaped the code: one lock shared by `push()` and `close()`, a clear end-of-queue marker, and `task.run()` outside the lock.

## What I learned

I learned that "no new tasks after `close()`" and "no lost tasks" are not two separate checks, as I wrote them in my list. They are one atomicity requirement: the check in `push()`, the enqueue, and the moment `close()` marks the end of the queue must happen under the same lock. Checking a flag in `push()` is not enough if shutdown can happen between the check and the enqueue.

I also learned that a lifecycle can be fully satisfied while the code still fails the given tests. My lifecycle said a task pushed after `close()` must be "rejected", but it did not say *how*: with an exception or silently. The AI chose an exception, and the provided test required the opposite. A lifecycle defines behavior, but the API contract (signatures, exceptions) has to be specified too.

## What I changed

The static review of the AI version against `lifecycle.md` passed row by row, so I kept its ideas: `FutureTask` for `threadIt`, one dedicated worker, a FIFO queue, one shared lock for `push()` and `close()`, `task.run()` wrapped in `try/catch` and executed outside the lock, no busy-waiting, no thread pool, and no self-`join()` when `close()` is called from the worker. I changed what the provided tests required, and simplified the worker:

- **`Q1a`:** `threadIt` is now an instance method, and the class has a `close()` method. `close()` stops new submissions (a later `threadIt` throws `RejectedExecutionException`) and waits for every thread that was already started. A `Set` of running threads, protected by a lock, tracks them. Like `Q1b`, `close()` called from inside a submitted `Callable` does not wait for itself.
- **`Q1b` rejection:** `push()` after `close()` is now a silent no-op. The task is never queued or run, which still matches the lifecycle row "it must not be queued or executed", but no exception is thrown, because `MainTrain1b` calls `push()` after `close()` without catching one.
- **`Q1b` worker:** I replaced the `ArrayDeque` with `wait()`/`notifyAll()` by a `LinkedBlockingQueue` and a `STOP` sentinel. The worker blocks in `queue.take()`, so there is still no busy-waiting. `close()` sets `accepting = false` and enqueues `STOP` under the same lock that `push()` uses. Because the queue is FIFO, `STOP` is behind every accepted task, and no task can be added after it. The worker exits only when it takes `STOP`, so the exit decision no longer needs its own lock-protected check.
- **Package:** both classes are now in `package test`, as the provided tests expect.

Both classes now pass 15 runtime tests, including a race test for `push()` vs. `close()`.

These changes have trade-offs, and I accepted them on purpose:

- **Silent rejection in `Q1b`:** a caller cannot tell that its task was dropped. `Q1a` still throws in the same situation, so the two classes report rejection differently. I accepted this because the provided test fixes `Q1b`'s behavior, and the comment in `push()` documents the decision.
- **`close()` waits forever if an accepted task never finishes** (in both classes). A timeout would avoid this, but the assignment requires running *all* tasks accepted before `close()`. Giving up after a timeout would break that requirement, so a stuck task is the caller's responsibility.
- **The worker is not a daemon thread, so it keeps the JVM alive until `close()` is called.** A daemon worker would let the program exit, but tasks still in the queue would be silently dropped when `main` ends. `close()` is the defined way to shut down, so I preferred an explicit shutdown over lost tasks.

## What I would do next time

Next time, I would write failure modes about **combinations** of operations, not only about each method on its own: what happens if `push()` and `close()` run at the same time, or if a task calls back into the Active Object.

I would also add to the lifecycle the events I left undefined this time: `close()` called from inside a task, an accepted task that never finishes, a program that never calls `close()`, and how a rejected task is reported. And I would give the AI the provided test files together with the lifecycle, so that its code matches the required API from the start.

## Exercise-specific insight

### Which failure mode was not obvious at first?

The failure mode I missed was **a race between `push()` and `close()`** (item 6 in `failure-modes-ai.md`).

My own list had "losing tasks when closing" and "accepting tasks after closing" as two separate problems. I assumed that a `closed` flag checked in `push()` would solve the second one. What I did not see is that the check, the enqueue, and the end of the queue must be decided atomically. Without that, this order of events is possible:

1. `push()` checks the flag and sees that the object is still open.
2. `close()` sets the flag.
3. The worker sees an empty, closed queue and exits.
4. `push()` adds its task to the queue.

The task was accepted without an error, but it never runs. It silently disappears.

### How it shaped the final solution

This failure mode determined the structure of the Active Object. In `final/`, `push()` checks `accepting` and enqueues inside one lock, and `close()` clears `accepting` and enqueues the `STOP` sentinel inside the same lock. So a task is either enqueued before `STOP`, and then the worker runs it before exiting, or it arrives after `close()` and is never queued. Step 4 above can no longer happen. A concurrency test confirmed this: in 300 rounds where four threads called `push()` while `close()` was called, every task pushed before `close()` started was executed, and no task ran after `close()` returned.

The same failure mode also shaped `Q1a.close()`: registering a new thread and checking `closed` happen under one lock, so a `threadIt` call either starts a thread that `close()` will wait for, or is rejected.

A second case appeared in neither list nor in my lifecycle: `close()` called from inside a task. A naive `close()` would `join()` the worker, which is the current thread, and wait for itself forever. The AI code checks `Thread.currentThread() == worker` and only starts the shutdown in that case, and I kept this check in `final/`. Next time I would define this case in the lifecycle explicitly.
