package test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Q3 {

    public static double calc(String expression) {
        List<String> postfix = toPostfix(expression);
        Deque<Expression> stack = new ArrayDeque<>();

        for (String token : postfix) {
            if (token.equals("~")) { // Unary minus: -x is 0 - x.
                if (stack.isEmpty()) {
                    throw new IllegalArgumentException("Invalid expression");
                }
                stack.push(new Minus(new Number(0), stack.pop()));
            } else if (isBinaryOperator(token)) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid expression");
                }
                Expression right = stack.pop();
                Expression left = stack.pop();

                switch (token) {
                    case "+": stack.push(new Plus(left, right)); break;
                    case "-": stack.push(new Minus(left, right)); break;
                    case "*": stack.push(new Mul(left, right)); break;
                    case "/": stack.push(new Div(left, right)); break;
                    default: throw new IllegalArgumentException("Unknown operator");
                }
            } else {
                stack.push(new Number(Double.parseDouble(token)));
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid expression");
        }
        return stack.pop().calculate();
    }

    private static List<String> toPostfix(String expression) {
        if (expression == null) {
            throw new IllegalArgumentException("Expression cannot be null");
        }

        List<String> output = new ArrayList<>();
        Deque<Character> operators = new ArrayDeque<>();
        boolean expectOperand = true;
        int i = 0;

        while (i < expression.length()) {
            char c = expression.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (Character.isDigit(c) || c == '.') {
                if (!expectOperand) {
                    throw new IllegalArgumentException("Missing operator");
                }
                int start = i;
                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    i++;
                }
                // Validate numeric text (including decimal values).
                String number = expression.substring(start, i);
                Double.parseDouble(number);
                output.add(number);
                expectOperand = false;
            } else if (c == '(') {
                if (!expectOperand) {
                    throw new IllegalArgumentException("Missing operator before '('");
                }
                operators.push(c);
                i++;
            } else if (c == ')') {
                if (expectOperand) {
                    throw new IllegalArgumentException("Missing operand before ')'");
                }
                while (!operators.isEmpty() && operators.peek() != '(') {
                    output.add(String.valueOf(operators.pop()));
                }
                if (operators.isEmpty()) {
                    throw new IllegalArgumentException("Unmatched ')'");
                }
                operators.pop(); // Discard '('.
                expectOperand = false;
                i++;
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                if (expectOperand) {
                    if (c == '-') {
                        operators.push('~'); // Higher-priority unary minus.
                    } else if (c != '+') {
                        throw new IllegalArgumentException("Missing operand before operator");
                    }
                    // Unary plus has no effect.
                } else {
                    while (!operators.isEmpty() && operators.peek() != '(' &&
                            precedence(operators.peek()) >= precedence(c)) {
                        output.add(String.valueOf(operators.pop()));
                    }
                    operators.push(c);
                    expectOperand = true;
                }
                i++;
            } else {
                throw new IllegalArgumentException("Unexpected character: " + c);
            }
        }

        if (expectOperand) {
            throw new IllegalArgumentException("Missing operand");
        }
        while (!operators.isEmpty()) {
            char operator = operators.pop();
            if (operator == '(') {
                throw new IllegalArgumentException("Unmatched '('");
            }
            output.add(String.valueOf(operator));
        }
        return output;
    }

    private static boolean isBinaryOperator(String token) {
        return token.equals("+") || token.equals("-") ||
               token.equals("*") || token.equals("/");
    }

    private static int precedence(char operator) {
        if (operator == '~') return 3;
        if (operator == '*' || operator == '/') return 2;
        if (operator == '+' || operator == '-') return 1;
        return 0;
    }
}
