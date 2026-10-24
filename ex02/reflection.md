# Reflection

## What I expected

I expected this exercise to be simpler than ex01. `MyStringBuilder` has only four small methods, and the test in `MainTrain` shows exactly how they are used. I assumed the main challenge would be getting the method chaining to work, and that the spec would mostly repeat what the assignment already said.

## What actually happened

The AI found more ambiguities than I did. My list in `assumptions-human.md` had three points: the parameter type of `apply`, the return type needed for chaining, and whether the methods modify the object or create a new one. The AI's list also raised null handling, possible overloads of `append` (for `char`, `int` or `Object`), and extra constructors.

After I wrote the spec, the AI implementation passed `MainTrain` on the first try. It used hand-written loops for every method: it swaps elements from both ends in `reverse`, loops with `set` in `apply`, and checks `null` explicitly in `apply`.

When I reviewed it, I found that the spec did not match the given skeleton. The spec asks for a `private` field and describes only a no-argument constructor. The skeleton marked "DO NOT Change" has a package-private field and a `char...` constructor, and the test depends on that constructor.

## Most important difference

The most important difference was **null handling**, which I had not thought about at all. My assumptions covered only what the example in the assignment showed. The AI also looked at inputs the example never uses, such as `append(null)` and `apply(null)`.

These cases matter because `MainTrain` does not test them, but the submission system's tests are described as more complex. Without a decision in the spec, `append(null)` would fail with a `NullPointerException` from `str.length()`. A user of the real `StringBuilder` would expect it to append `"null"`.

## What I learned

I learned that a spec is useful mainly for the decisions that the example and the test do not force. Behavior that the test checks will come out right either way. Behavior that the test leaves open depends entirely on what the spec says, or on what the implementer guesses.

I also learned that a spec should describe the result, not the algorithm. My spec said "iterates through the internal ArrayList". The AI implemented that wording literally and missed simpler library solutions.

## What I changed

In the final version (`final/test/MyStringBuilder.java`):

- `apply` is now `buff.replaceAll(function)`. `ArrayList.replaceAll` already takes a `UnaryOperator<E>` and throws `NullPointerException` when its argument is `null`, so the explicit check is no longer needed.
- `reverse` is now `Collections.reverse(buff)` instead of swapping elements by hand.
- `append` loops over `str.toCharArray()` and still turns `null` into `"null"`.
- I removed the unused `java.util.List` import.
- I left the "DO NOT Change" section exactly as given.

The final version passes `MainTrain`. I also checked edge cases: `append(null)`, an empty builder, a single character, and `apply(null)` on both an empty and a non-empty builder.

## What I would do next time

Next time, I would read the given skeleton before writing the spec. That way the spec would describe the existing fields and constructors instead of contradicting them.

I would also go over each method and ask what happens on `null`, on empty input, and on inputs the example does not show. Finally, I would describe the required result in the spec and leave the algorithm open, so that existing library methods can be used.

## Exercise-specific insight

### Which spec decision actually affected the implementation?

The decision that affected the code most was **the type of the `apply` parameter: `UnaryOperator<Character>`**.

The original assignment only said "given a suitable function". The test lambda `c -> (char)(c - ('a' - 'A'))` compiles with several choices: `UnaryOperator<Character>`, `Function<Character, Character>`, or a custom interface such as `interface CharOp { char apply(char c); }`. So `MainTrain` cannot tell these choices apart. Only the spec decides between them.

This decision determined the method signature. It also determined whether library code could be reused. `ArrayList<Character>.replaceAll` accepts exactly a `UnaryOperator<Character>`, so `apply` became one line.

**What would happen if it were left undefined?**

- The AI might have picked `Function<Character, Character>` or a custom interface. The code would still pass `MainTrain`, but `replaceAll` could not be called directly. It would need an adapter such as `buff.replaceAll(function::apply)`, or a hand-written loop.
- A test in the submission system that passes a `UnaryOperator` variable to `apply` would fail to compile against a different parameter type. A test that passes a plain lambda would still compile, so this risk is easy to miss.
- With a custom interface, users of the class would have to learn a new type instead of using a standard one from `java.util.function`.

The **in-place mutation** decision is a second example, and a more surprising one. Suppose it were left undefined, and each method returned a new `MyStringBuilder` instead of `this`. The chained test `sb.append("abc").append("def").reverse()...` would still print `FEDCBA`, because the chain always uses the returned object. But code that ignores the return value, like `sb.append("abc"); sb.toString();`, would return an empty string. `MainTrain` does not check this case, so only the spec prevents the bug.

### Was the spec precise enough?

For the behavior, mostly yes. It settled the important ambiguities: the methods change the object in place and return `this`, `apply` takes a `UnaryOperator<Character>`, and it says what happens on `null`. The AI implementation passed `MainTrain` on the first try.

It was not fully precise, though:

- **It contradicts the given code.** The spec asks for a `private` field and describes only a no-argument constructor. The skeleton marked "DO NOT Change" has a package-private `buff` field and a second constructor, `MyStringBuilder(char... ts)`. The test uses that constructor in almost every check. The spec should have described the skeleton as it is.
- **It leaves one edge case open.** It does not say what happens if the function passed to `apply` returns `null` (for example `c -> null`). The `null` is stored in the list, and later `toString()` and `reverse()` fail with a `NullPointerException` while unboxing it.
- **It describes how instead of what.** "Iterates through the internal ArrayList and applies the function to every character" describes a loop, not a result. That wording pushes the implementation toward a hand-written loop and hides the reuse described below.

### A reuse opportunity that was not obvious from the original wording

The original assignment only said "given a suitable function". Once we chose `UnaryOperator<Character>`, `apply` turned out to be exactly what `ArrayList.replaceAll(UnaryOperator<E>)` already does. The method becomes one line, `buff.replaceAll(function)`, and the `NullPointerException` the spec asks for comes free with it, because `replaceAll` checks its argument first.

In the same way, `reverse` is `Collections.reverse(buff)`, so we don't need to swap elements by hand.

So the choice of interface wasn't just a matter of style. Choosing the JDK's standard functional interface, instead of a custom one, is what let us reuse the collection's existing behavior. The final version uses both library methods.

### A possible generalization (not implemented)

`append` could accept `CharSequence` instead of `String`. Then it would also accept `StringBuilder`, and even `MyStringBuilder` itself if the class implemented `CharSequence`. Every existing call would still compile. I did not change it, because the spec and the test define `append(String)`.
