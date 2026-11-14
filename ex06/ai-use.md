# AI Use

## Model A – Developer

### Tool / Model

ChatGPT 6

### Role

Developer

### Main prompt(s)

"Implement `ParMaxSearcher` in package `test` as a `RecursiveTask<Integer>` that finds the maximum value in a `BinTree` using a `ForkJoinPool`. In each step, the search in the left subtree must run in another thread of the pool. Return the full `ParMaxSearcher.java` file."

### Inputs provided

- Original assignment text of exercise 6
- `BinTree.java` (given, not to be edited)
- `ParMaxSearcher.java` skeleton

### Context

Fresh context: yes

### Notes

The output was saved in `ai-implementation/` without changes.

## Model B – Reviewer

### Tool / Model

Gemini Hydrogen

### Role

Reviewer

### Main prompt(s)

"Review the following implementation of the exercise. Focus on correctness, actual parallelism, unnecessary overhead, and compliance with the exercise requirements."

### Inputs provided

- Original assignment text of exercise 6
- Model A's `ParMaxSearcher.java`

### Context

Fresh context: yes (a different model, which did not see model A's conversation)

### Notes

The review was saved in `review-ai.md`. The reviewer did not receive `MainTrain.java`, which explains why it guessed that "grading tests often verify task creation counts" instead of referring to the actual steal-count check. My decisions on each comment are in `review-student.md`.
