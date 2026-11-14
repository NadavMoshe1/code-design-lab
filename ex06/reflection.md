# Reflection

## What I expected

I expected the independent AI reviewer to find real problems in model A's `ParMaxSearcher`: a wrong `fork()`/`join()` order, a missing case, or code that does not actually run in parallel. I thought my main job would be to decide which of its fixes to apply.

## What actually happened

Model A's implementation was correct. It forks the left subtree, computes the right subtree on the current thread, and then joins the left one. It passed the provided `MainTrain` in 20 of 20 runs.

The reviewer found no bugs and concluded that "no changes are needed". Most of its comments were accurate, but some were stated with more confidence than the code or the test supports: that the `final` field makes the class thread-safe, that the solution is an "optimal" use of the cores, and that it "should pass all tests and earn full credit". In `review-student.md` I accepted the accurate comments and only partially accepted these. `final/` is identical to `ai-implementation/`.

## Most important difference

The most important difference was between the reviewer's reasoning and what the test actually checks, in the comment about a sequential threshold.

The reviewer correctly noted that creating a task for every node, down to the leaves, costs more than traversing small subtrees sequentially. But it then said that a threshold must not be added because "automated grading tests often verify task creation counts". That is a guess. `MainTrain` does not count tasks. It checks `fjp.getStealCount() > 1` on a tree of depth 10 (2,047 nodes), and that turned out to be the real reason not to add a threshold.

## What I learned

I learned that a reviewer's conclusion can be right while its reasoning is wrong, so I have to check the reasoning against the real code and test, not only the conclusion. Here, "don't add a threshold" was the right decision for this test, but for a different reason than the one the reviewer gave, and the reviewer's own claim of "optimal" parallelism was not true for large trees.

I also learned what actually makes this code thread-safe. A `final` field only guarantees that other threads see the `tree` reference correctly. The code is safe because the tree is only read and never modified, and because `fork()` and `join()` create a happens-before relation between a subtask and the thread that reads its result.

## What I changed

I did not change the code: `final/` is identical to `ai-implementation/`.

- **Forking at every node:** I kept it, because the provided test checks the steal count on a small tree, and the measurements below show that a threshold can make that check fail.
- **`new ParMaxSearcher(right).compute()`:** I kept it instead of a private helper method. It allocates an extra task object for every right child, but it keeps the code short, and the cost is negligible for the tree size in the test.

The trade-off is that the solution is tuned for the provided test, not for large trees. For a tree with millions of nodes, forking only down to a certain depth and then traversing sequentially would be the better design.

## What I would do next time

Next time, I would give the reviewer the provided `MainTrain` together with the code, and ask it to support each claim with the part of the code or the test it relies on. A claim like "grading tests often verify task creation counts" would then be easy to reject at once.

I would also not accept performance claims such as "optimal" or "easily meeting the 1-second timeout" without a measurement, from the reviewer or from myself.

## Exercise-specific insight

### Which reviewer comment was most useful?

The comment about **granularity (a sequential threshold)** was the most useful. It was the only comment that pointed at a real engineering trade-off instead of confirming the code. When I measured it, both sides of the trade-off were real:

- **On a large tree, the threshold wins.** On a tree of depth 22 (about 8 million nodes), forking at every node took 34 ms, which is slower than a plain sequential traversal (22 ms). A version that forks only in the top levels and traverses every subtree of depth 8 or less sequentially took 9 ms.
- **On the test's tree, the threshold fails.** On the `MainTrain` tree of depth 10, fewer forks mean fewer chances for another thread to steal work. With only 15 forked tasks, the steal count was ≤ 1 in 9 of 300 runs. With 7 forked tasks, it failed in 223 of 300 runs. Forking at every node never failed.

### Which comments did I reject, and how did I check their logic?

I partially rejected three claims:

1. **"The `final` field ensures thread-safety."** I checked what is actually shared between threads. `final` protects only the reference. Thread-safety comes from the tree being read-only and from the happens-before relation of `fork()`/`join()`. The conclusion (the class is safe) is right, but the reason given is not sufficient.
2. **"The solution should pass all tests and earn full credit."** I read `MainTrain`: it requires `getStealCount() > 1`, which depends on how the pool schedules the tasks, not only on the code. The code passed 20 of 20 runs, but no reviewer can guarantee a result that depends on scheduling.
3. **The reason for not adding a threshold.** I accepted the conclusion and rejected the reasoning. I checked it by reading what `MainTrain` really tests (steal count, not task count) and by measuring different thresholds, as shown above.
