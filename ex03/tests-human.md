# tests human

Test Scenarios - Exercise 3 (Interpreter Pattern)

Here are the basic test scenarios for the calc function, covering basic arithmetic, precedence, and the assignment's provided example:

Single Number

Input: "5"

Expected Output: 5.0

Reasoning: Verifies that the system can read and parse a single number without any operators.

Simple Addition

Input: "2+3"

Expected Output: 5.0

Reasoning: Tests the basic addition operator.

Simple Subtraction

Input: "10-4"

Expected Output: 6.0

Reasoning: Tests the basic subtraction operator.

Simple Multiplication

Input: "3*4"

Expected Output: 12.0

Reasoning: Tests the basic multiplication operator.

Simple Division

Input: "8/2"

Expected Output: 4.0

Reasoning: Tests the basic division operator (without complex remainders for now).

Operator Precedence

Input: "2+3*4"

Expected Output: 14.0

Reasoning: Ensures the Shunting-yard algorithm correctly prioritizes multiplication over addition.

Parentheses Priority

Input: "(2+3)*4"

Expected Output: 20.0

Reasoning: Verifies that parentheses are given the highest priority and correctly alter the order of operations.

Assignment Example

Input: "3+(4/2)*5"

Expected Output: 13.0

Reasoning: Validates the exact example provided in the assignment PDF instructions.