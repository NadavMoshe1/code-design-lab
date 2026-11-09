# guardrails

1. Avoid busy-waiting: Do not use tight loops that repeatedly check whether the Future is done, or arbitrary sleep() calls to guess when it will finish.
2. Keep shared data thread-safe: The result and completion state may be accessed by different threads, so updates must be visible and consistent.
3. Avoid race conditions: Make sure that an observer registered around the time of completion is not accidentally missed because of timing.
4. Prevent duplicate notifications: Each observer should not be notified multiple times for the same completion, and notifications should happen only after the result is ready.
5. Do not hold locks during callbacks: An observer may perform slow operations or call back into the object, so calling observers while holding a lock could cause deadlocks.
6. Handle failures safely: If the wrapped Future is cancelled, interrupted, or fails, do not invent a result, notify observers as if a value arrived, or get stuck in an endless loop.
7. Keep the solution simple: Avoid unnecessary threads, external libraries, hard-coded timeouts, or changes to the original Future.
These rules focus on avoiding problematic implementations rather than repeating the exercise's functional requirements.