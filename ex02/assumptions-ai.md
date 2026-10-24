# assumptions ai

TODO: Complete this file at the required stage of the exercise.
Based on the assignment description you provided, here are the missing, ambiguous, or unspecified details that require a decision before or during implementation:

**1. The exact interface for `apply**`

* **The Ambiguity:** The instructions mention "given a suitable function" (*פונקציה מתאימה*) but do not specify the exact Java functional interface.
* **The Decision:** You need to decide whether to use Java's built-in `java.util.function.UnaryOperator<Character>`, `java.util.function.Function<Character, Character>`, or if you are expected to define a custom interface. (Given the lambda in the example, `UnaryOperator<Character>` is the most standard choice).

**2. Null Handling**

* **The Ambiguity:** The behavior for `null` inputs is entirely unspecified.
* **The Decision:**
* What should `append(null)` do? The standard Java `StringBuilder` appends the literal 4-character string `"null"`. Should yours do the same, throw a `NullPointerException`, or do nothing?
* What should happen if `apply(null)` is called?



**3. In-place Mutation vs. Returning a New Instance**

* **The Ambiguity:** While fluent programming requires returning `this` to chain methods, the instructions don't explicitly state whether methods like `reverse()` and `apply()` should mutate the internal `ArrayList<Character>` *in-place* or if they should return a *new* `MyStringBuilder` object.
* **The Decision:** Standard `StringBuilder` mutates in-place, which is highly likely what is expected here for performance reasons, but you must ensure your implementation consistently mutates the existing state before returning `this`.

**4. Overloading of the `append` method**

* **The Ambiguity:** The text strictly says "given a String, the method will add it..." (*בהינתן מחרוזת*).
* **The Decision:** Are you only expected to support `append(String)`, or will the hidden tests ("הבדיקה במוד ההגשה מורכבת יותר") try to append a single `char`, an `int`, or an `Object`, just like the real `StringBuilder` does?

**5. Constructors**

* **The Ambiguity:** The example only demonstrates the default, no-argument constructor `new MyStringBuilder()`.
* **The Decision:** Will the test environment expect a parameterized constructor like `new MyStringBuilder("initial string")`? If not explicitly required, you must decide whether to implement it for completeness or stick strictly to the default one.