# tests final

## Ex3 – Test Scenarios, Inputs, Expected Results & Behavior

The purpose of these tests is to verify that the Q3.calc(String expression) method correctly parses and evaluates arithmetic expressions using the Interpreter Pattern and the Shunting-yard algorithm.

### 1. Test Scenarios – Inputs and Expected Results

| # | Test Scenario | Input (expression) | Expected Result |
|---|---|---|---|
| 1 | Single number | `5` | 5.0 |
| 2 | Simple addition | `3+4` | 7.0 |
| 3 | Simple subtraction | `10-6` | 4.0 |
| 4 | Simple multiplication | `3*5` | 15.0 |
| 5 | Simple division | `12/4` | 3.0 |
| 6 | Floating-point division | `7/2` | 3.5 |
| 7 | Multiplication precedence | `2+3*4` | 14.0 |
| 8 | Parentheses precedence | `(2+3)*4` | 20.0 |
| 9 | Multiple operations | `10+6/2*3` | 19.0 |
| 10 | Left-associative subtraction | `10-3-2` | 5.0 |
| 11 | Left-associative division | `20/5/2` | 2.0 |
| 12 | Multiplication and division | `8/2*3` | 12.0 |
| 13 | Nested parentheses | `2*(3+(4*5))` | 46.0 |
| 14 | Negative result | `4-10` | -6.0 |
| 15 | Zero value | `0*9+5` | 5.0 |
| 16 | Multi-digit numbers | `12+34` | 46.0 |
| 17 | Assignment example | `3+(4/2)*5` | 13.0 |
| 18 | Multiple parentheses | `((2+3))` | 5.0 |
| 19 | All four operators | `10+6*2-8/4` | 20.0 |
| 20 | Complex nested expression | `2*(3+4*(5-2))` | 30.0 |

### 2. Expected Behavior

| Test Scenario | Expected Behavior |
|---|---|
| Basic arithmetic | Correctly perform addition, subtraction, multiplication, and division. |
| Operator precedence | Evaluate multiplication and division before addition and subtraction. |
| Parentheses | Evaluate expressions inside parentheses first. |
| Nested parentheses | Correctly handle multiple levels of parentheses. |
| Left associativity | Evaluate operators with equal precedence from left to right. |
| Multi-digit numbers | Correctly recognize numbers containing multiple digits. |
| Floating-point results | Return decimal results without integer truncation. |
| Negative results | Correctly return negative values when applicable. |
| Single number | Return the number itself without requiring an operator. |
| Complex expressions | Correctly evaluate expressions containing multiple operators and parentheses. |

### 3. Edge Cases – Additional Tests

These cases are useful for exploring the implementation's limitations. However, the assignment does not explicitly define the expected behavior for all of them.

| # | Input | Test Scenario | Behavior to Check |
|---|---|---|---|
| 1 | `5/0` | Division by zero | Check whether the implementation returns Infinity or handles it differently. |
| 2 | `0/0` | Zero divided by zero | Check whether the implementation returns NaN. |
| 3 | `-5+3` | Unary minus | Check whether negative input numbers are supported. |
| 4 | `2.5*4` | Decimal input | Check whether decimal numbers are supported. |
| 5 | `3 + 4` | Whitespace | Check whether spaces are ignored. |
| 6 | `(2+3` | Unbalanced parentheses | Check how invalid parentheses are handled. |
| 7 | `2++3` | Invalid operator sequence | Check how invalid expressions are handled. |
| 8 | `""` | Empty expression | Check how empty input is handled. |
