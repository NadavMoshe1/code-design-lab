# spec
1. Overview
MyStringBuilder is a custom, lightweight imitation of Java's native StringBuilder. It relies internally on an ArrayList<Character> to store string data and supports method chaining (fluent programming) for sequential text manipulations.

2. Internal State
Data Structure: The class must contain a private field of type ArrayList<Character> to hold the sequence of characters.

Initialization: The class must provide a default, no-argument constructor that initializes an empty ArrayList<Character>.

3. Design Decisions & Behaviors (Resolved Ambiguities)
To ensure consistent implementation, the following design decisions have been finalized:

Mutability: All transformation methods (append, reverse, apply) must modify the existing object's internal state in-place. Do not create or return a new MyStringBuilder instance.

Fluent Interface: To support method chaining, append, reverse, and apply must return a reference to the current object (return this;). The return type for these methods must be MyStringBuilder.

Apply Interface: The apply method will accept a java.util.function.UnaryOperator<Character> as its parameter to handle the character-by-character transformation.

Null Handling:

If append(null) is called, it should append the literal string "null" to the list (matching native StringBuilder behavior).

If apply(null) is called, it should throw a NullPointerException.

4. Method Specifications
public MyStringBuilder append(String str)
Behavior: Appends each character of the provided string to the end of the internal ArrayList. If str is null, append the characters 'n', 'u', 'l', 'l'.

Returns: this (the current MyStringBuilder instance).

public MyStringBuilder reverse()
Behavior: Reverses the order of the characters currently held in the ArrayList in-place.

Returns: this (the current MyStringBuilder instance).

public MyStringBuilder apply(UnaryOperator<Character> function)
Behavior: Iterates through the internal ArrayList and applies the provided functional interface to every character, updating the list in-place with the resulting characters.

Returns: this (the current MyStringBuilder instance).

Exceptions: Throws NullPointerException if function is null.

public String toString()
Behavior: Constructs and returns a standard Java String based on the current sequence of characters in the ArrayList.

Returns: A new String object.