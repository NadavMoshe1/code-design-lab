# review student

### 1. Compliance – Partially Accept
The solution meets the requirements and uses ForkJoin correctly. However, `final` alone does not guarantee thread safety.

### 2. Correctness – Accept
The code handles null trees, leaf nodes, and nodes with one child. The maximum calculation is correct, so no changes are needed.

### 3. Fork-Join Implementation – Accept
The order of `fork()`, `compute()`, and `join()` is correct and allows parallel execution.

### 4. Actual Parallelism – Partially Accept
ForkJoinPool allows parallel execution, but it doesn't guarantee that every task runs on a different thread or finishes within one second.

### 5. Object Creation – Accept
A helper method could reduce object creation, but I prefer keeping the current code because it is simple and readable.

### 6. Sequential Threshold – Partially Accept
Creating many tasks adds overhead, but the exercise requires using ForkJoin for the left subtree in every iteration, so I won't add a threshold.

### 7. Autoboxing – Accept
Using `int` for intermediate results avoids unnecessary conversions and is more efficient.

### 8. Final Recommendation – Partially Accept
The solution follows the requirements, but a full grade cannot be guaranteed without testing.

### Conclusion
I accepted most of the reviewer's comments and decided to keep the code unchanged because it meets the exercise requirements.
