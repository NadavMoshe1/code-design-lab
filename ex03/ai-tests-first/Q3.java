package test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Q3 {
    public static double calc(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Expression is empty");
        }

        // Shunting-yard: convert the infix expression to postfix notation.
        List<String> postfix = new ArrayList<>();
        Deque<Character> operators = new ArrayDeque<>();
        boolean expectOperand = true;

        for (int i = 0; i < expression.length();) {
            char c = expression.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (Character.isDigit(c) || c == '.') {
                if (!expectOperand) {
                    throw new IllegalArgumentException("Missing operator at position " + i);
                }
                int start = i;
                int dots = 0;
                int digits = 0;
                while (i < expression.length()) {
                    char current = expression.charAt(i);
                    if (Character.isDigit(current)) {
                        digits++;
                    } else if (current == '.') {
                        dots++;
                    } else {
                        break;
                    }
                    i++;
                }
                if (digits == 0 || dots > 1) {
                    throw new IllegalArgumentException("Invalid number at position " + start);
                }
                String number = expression.substring(start, i);
                try {
                    Double.parseDouble(number);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number: " + number, e);
                }
                postfix.add(number);
                expectOperand = false;
                continue;
            }

            if (c == '(') {
                if (!expectOperand) {
                    throw new IllegalArgumentException("Missing operator before '('");
                }
                operators.push(c);
                i++;
                continue;
            }

            if (c == ')') {
                if (expectOperand) {
                    throw new IllegalArgumentException("Missing operand before ')'");
                }
                while (!operators.isEmpty() && operators.peek() != '(') {
                    postfix.add(String.valueOf(operators.pop()));
                }
                if (operators.isEmpty()) {
                    throw new IllegalArgumentException("Unbalanced parentheses");
                }
                operators.pop(); // Discard the matching '('.
                expectOperand = false;
                i++;
                continue;
            }

            if (c == '+' || c == '-' || c == '*' || c == '/') {
                if (expectOperand) {
                    if (c == '-') {
                        // Unary minus: represented by '~' in the postfix list.
                        operators.push('~');
                        i++;
                        continue;
                    }
                    throw new IllegalArgumentException("Missing left operand at position " + i);
                }

                // All binary operators are left-associative.
                while (!operators.isEmpty() && operators.peek() != '('
                        && precedence(operators.peek()) >= precedence(c)) {
                    postfix.add(String.valueOf(operators.pop()));
                }
                operators.push(c);
                expectOperand = true;
                i++;
                continue;
            }

            throw new IllegalArgumentException("Unknown character '" + c + "' at position " + i);
        }

        if (expectOperand) {
            throw new IllegalArgumentException("Expression ends without an operand");
        }

        while (!operators.isEmpty()) {
            char operator = operators.pop();
            if (operator == '(') {
                throw new IllegalArgumentException("Unbalanced parentheses");
            }
            postfix.add(String.valueOf(operator));
        }

        // Build an Interpreter expression tree from the postfix notation.
        Deque<Expression> operands = new ArrayDeque<>();
        for (String token : postfix) {
            if (token.equals("~")) {
                if (operands.isEmpty()) {
                    throw new IllegalArgumentException("Missing unary operand");
                }
                operands.push(new Minus(new Number(0), operands.pop()));
            } else if (token.length() == 1 && isBinaryOperator(token.charAt(0))) {
                if (operands.size() < 2) {
                    throw new IllegalArgumentException("Missing binary operands");
                }
                Expression right = operands.pop();
                Expression left = operands.pop();
                switch (token.charAt(0)) {
                    case '+': operands.push(new Plus(left, right)); break;
                    case '-': operands.push(new Minus(left, right)); break;
                    case '*': operands.push(new Mul(left, right)); break;
                    case '/': operands.push(new Div(left, right)); break;
                    default: throw new IllegalStateException("Unexpected operator");
                }
            } else {
                operands.push(new Number(Double.parseDouble(token)));
            }
        }

        if (operands.size() != 1) {
            throw new IllegalArgumentException("Malformed expression");
        }

        double result = operands.pop().calculate();
        // The supplied MainTrain compares double results using exact equality.
        // Remove tiny floating-point error only when very close to an integer.
        if (Double.isFinite(result)) {
            double rounded = Math.rint(result);
            if (Math.abs(result - rounded) <= 4 * Math.ulp(result)) {
                return rounded;
            }
        }
        return result;
    }

    private static boolean isBinaryOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private static int precedence(char operator) {
        switch (operator) {
            case '~': return 3; // Unary minus.
            case '*':
            case '/': return 2;
            case '+':
            case '-': return 1;
            default: return 0;
        }
    }
}
