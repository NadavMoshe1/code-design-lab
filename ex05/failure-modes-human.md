# failure modes human

1. **Running the function on the main thread:** In `threadIt`, we might call the function directly instead of running it on a new thread. This would block the main thread until the function finishes.

2. **Returning the result too early:** The `Future` might return a value before the function finishes its calculation, resulting in `null` or an incorrect result.

3. **Running tasks at the same time:** In the `Active Object`, we might create a new thread for every `push` call instead of running the tasks one by one on the same thread.

4. **Losing tasks when closing:** If `close()` stops the worker thread immediately, tasks that were already added to the queue might never run.

5. **Accepting tasks after closing:** If we do not check whether the `Active Object` is closed, new tasks might still be accepted after `close()` is called, which breaks the requirements.
