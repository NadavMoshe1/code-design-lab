# lifecycle


## 1. `threadIt` / `Future<V>`

Calling `threadIt(f)` starts `f` in a **new thread** and immediately returns a `Future<V>` representing its result. The caller does not need to wait for `f` to finish before receiving the future.

| State | Event | Expected behavior | Next state |
|---|---|---|---|
| Created | `threadIt(f)` is called | Create a future and start a separate thread for `f`; return the future to the caller. | Running |
| Running | The caller calls `get()` before `f` finishes | `get()` waits until the computation is finished; it must not return an unfinished result. | Running (until completion) |
| Running | `f` returns a value `V` | Store the value and notify any threads waiting for it. | Completed |
| Running | `f` throws an exception | Record the failure and notify waiting threads; do not leave them waiting forever. | Failed |
| Completed | The caller calls `get()` | Return the stored result, including if its value is `null`. | Completed |
| Failed | The caller calls `get()` | Report the computation failure according to the `Future` API (for example, via `ExecutionException`). | Failed |

**Important:** Returning the `Future` immediately is correct; returning a result from `get()` before the computation completes is not. If the implementation supports cancellation, its behavior should also follow the Java `Future` contract.

## 2. Active Object

An Active Object has a task queue and **one dedicated worker thread**. Each accepted `Runnable` is executed by that worker, one task at a time. Calling `close()` starts an orderly shutdown: finish every task accepted before closing, and reject new tasks afterward.

| State | Event | Expected behavior | Next state |
|---|---|---|---|
| Open / Idle | `push(task)` is called | Accept the task and add it to the queue; wake the worker if it is waiting. | Open / Busy |
| Open / Busy | `push(task)` is called | Add the task to the queue without starting another worker thread. | Open / Busy |
| Open / Busy | The current task finishes | The same worker runs the next queued task. Tasks never run simultaneously. | Open / Busy or Open / Idle |
| Open / Idle | The queue is empty | The worker waits for new tasks instead of repeatedly checking in a busy loop. | Open / Idle |
| Open / Idle or Busy | `close()` is called | Stop accepting tasks, but keep the worker alive long enough to finish all previously accepted tasks. | Closing |
| Closing | `push(task)` is called | Reject the new task; it must not be queued or executed. | Closing |
| Closing | The worker finishes the final queued task | Exit the worker thread cleanly. | Closed |
| Closed | `push(task)` is called | Reject the task; no new execution is allowed. | Closed |
| Closed | `close()` is called again | Remain closed without restarting the worker. | Closed |

### Concurrency and ordering expectations

- **Single worker:** Even if multiple threads call `push()`, tasks must execute serially on the same worker thread.
- **No lost tasks:** A task successfully accepted before `close()` must be executed before shutdown finishes.
- **Atomic `push` vs. `close`:** When these calls happen concurrently, each task must be clearly either accepted before closing (and executed) or rejected after closing. No task may silently disappear.
- **Failure isolation:** If one `Runnable` throws an exception, the worker should handle it so that later accepted tasks can still run.
- **Shutdown completion:** Once the Active Object is fully closed, its worker has stopped and no queued tasks remain.

The implementation must not use `ExecutorService` or another thread pool for the Active Object.
