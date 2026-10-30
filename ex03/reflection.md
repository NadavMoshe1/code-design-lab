# Reflection

## What I expected

I expected the two AI versions to be almost the same. `Q3.calc` is a well-known problem: the Shunting-yard algorithm converts the expression to postfix, and the Interpreter classes evaluate it. I assumed that giving the AI the tests first would mostly help it pass them, and that without the tests it would still write a correct calculator, maybe with less input validation.

## What actually happened

My own tests (`tests-human.md`) had eight scenarios: a single number, the four operators, precedence, parentheses, and the example from the assignment. All of them are valid expressions. The AI's proposal (`tests-ai.md`) had 20 valid scenarios, a table of expected behaviors, and eight edge cases: division by zero, `0/0`, unary minus, decimal input, whitespace, unbalanced parentheses, `2++3`, and an empty expression. I adopted the AI's set as `tests-final.md`, because it covered everything in mine and much more.

Both AI versions passed all 20 scenarios in `tests-final.md`. The Interpreter classes (`Expression`, `Number`, `BinaryExpression`, `Plus`, `Minus`, `Mul`, `Div`) were identical in both versions. All the differences were in `Q3.java`, and they showed up only in the edge cases.

- **ai-tests-first** wrote one long `calc` method. It checked for an empty expression, validated numbers itself (`1.2.3` and `.` raise an `IllegalArgumentException` with a position), and rejected `2++3`. It also added code that rounds any result that is very close to an integer. Its own comment says it did this because `MainTrain` compares `double` results with exact equality. Because of it, `0.1*3*10` returns `3.0` and not `3.0000000000000004`.
- **ai-code-first** had a cleaner structure, with a separate `toPostfix` method. It was more permissive: it accepted a unary plus, so `2++3` returned `5.0`. A malformed number escaped as a raw `NumberFormatException`, and an empty expression gave the unclear message "Missing operand".

## Most important difference

The most important difference was **how each version handled invalid input**.

The edge-case table in `tests-final.md` lists `2++3` as an "Invalid operator sequence" and `""` as an "Empty expression", but under "Behavior to check" it does not say what the result should be. The tests-first AI saw these cases and made a decision for each one: it rejects them with a clear error. The code-first AI never saw them, so it made its own choices, and one of them was surprising: `2++3 = 5.0`.

The tests also had a negative effect. The rounding in the tests-first version exists only to make exact `double` comparisons pass. It hides real floating-point results, so it is code written for the test rather than for the calculator.

## What I learned

I learned that writing tests before the code changes the code the AI writes, not only whether it passes. Every case in the test set made the AI think about that input and handle it on purpose. Even when an edge case had no expected result, the AI still had to make a decision, and at least it made it on purpose.

I also learned that my own tests covered only the happy path. Every case that actually changed the implementation came from the AI's proposed tests, not from mine. Asking a second AI for additional tests was the step that made the difference.

Finally, tests can pull the implementation in the wrong direction. When the AI knows exactly how it will be checked, it may add code that only makes the check pass, like rounding the result. An implementation that passes every test is not automatically a good implementation, so I still have to review the code.

## What I changed

In the final version (`final/test/`):

- I kept the identical Interpreter classes as they are.
- I took the structure from ai-code-first, and split it further into `toPostfix` (Shunting-yard) and `buildTree` (building the Interpreter tree).
- I took the validation from ai-tests-first: a clear error for an empty expression, an `IllegalArgumentException` for malformed numbers, and the position in every error message.
- I do not allow a unary plus, so `2++3` is an error, because `tests-final.md` describes it as an invalid operator sequence. Unary minus (`-5+3`, `2*-3`) is still supported.
- I removed the rounding to the nearest integer, so `calc` returns the real `double` result.
- I removed the stack-size checks in `buildTree`. The `expectOperand` check in `toPostfix` already rejects every malformed expression, so those checks could never fail.

The final version passes all 20 scenarios. I also ran it on all eight edge cases and a few more: division by zero (`Infinity`), `0/0` (`NaN`), unary minus, decimals, spaces, unbalanced parentheses, `2++3`, an empty string, `1.2.3`, `()` and `3 4`.

## What I would do next time

Next time, I would write my own tests beyond the happy path: invalid input, empty input, and operator sequences. These are the cases where implementations differ.

I would also write an expected result for every edge case, not only "check how it is handled". An open edge case lets each implementation choose differently, as `2++3` showed.

And I would tell the AI not to change results just to pass the tests, and always run both versions on inputs that are not in the tests.

## Exercise-specific insight

### Which behavior appeared in tests-first because it was defined in the tests?

The clearest example is the **invalid operator sequence `2++3`** (edge case 7 in `tests-final.md`).

- **ai-code-first** returns `5.0`. It treats the second `+` as a unary plus and silently accepts the expression. Nothing in the original assignment says whether this is allowed, so the AI decided by itself, and nobody would notice without a test.
- **ai-tests-first** throws `IllegalArgumentException: Missing left operand at position 2`. The test named this input an "invalid operator sequence", so the AI treated it as an error.

A second example is the **empty expression `""`** (edge case 8). Both versions throw, but code-first reaches it by accident: the parser ends while still expecting an operand and reports "Missing operand". Tests-first checks for it explicitly at the start and reports "Expression is empty".

The other edge cases did not separate the two versions. Both return `Infinity` for `5/0` and `NaN` for `0/0`, both support unary minus, decimals and whitespace, and both reject `(2+3`. So the tests mattered exactly where the original assignment was silent about **what counts as an invalid expression**.

### What the tests did not decide

The tests only said "check how it is handled", so they forced the AI to consider each edge case but did not dictate the answer. For `2++3` the tests-first AI chose to reject it, and I kept that choice in `final/`, but another AI could have read the same test and accepted it. In the final version I made the decision explicit: every invalid expression throws an `IllegalArgumentException` with a clear message, while division by zero is not an error and returns `Infinity` or `NaN`, as Java's `double` arithmetic does.
