package test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Q3 {

    public static double calc(String expression) {
        List<String> postfix = toPostfix(expression);
        return buildTree(postfix).calculate();
    }

    // Shunting-yard: convert the infix expression to postfix notation.
    private static List<String> toPostfix(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Expression is empty");
        }

        List<String> output = new ArrayList<>();
        Deque<Character> operators = new ArrayDeque<>();
        boolean expectOperand = true;
        int i = 0;

        while (i < expression.length()) {
            char c = expression.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
            } else if (Character.isDigit(c) || c == '.') {
                if (!expectOperand) {
                    throw new IllegalArgumentException("Missing operator at position " + i);
                }
                int start = i;
                while (i < expression.length()
                        && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    i++;
                }
                output.add(parseNumber(expression.substring(start, i), start));
                expectOperand = false;
            } else if (c == '(') {
                if (!expectOperand) {
                    throw new IllegalArgumentException("Missing operator before '(' at position " + i);
                }
                operators.push(c);
                i++;
            } else if (c == ')') {
                if (expectOperand) {
                    throw new IllegalArgumentException("Missing operand before ')' at position " + i);
                }
                while (!operators.isEmpty() && operators.peek() != '(') {
                    output.add(String.valueOf(operators.pop()));
                }
                if (operators.isEmpty()) {
                    throw new IllegalArgumentException("Unmatched ')' at position " + i);
                }
                operators.pop(); // Discard the matching '('.
                expectOperand = false;
                i++;
            } else if (isBinaryOperator(c)) {
                if (expectOperand) {
                    if (c != '-') {
                        throw new IllegalArgumentException("Missing left operand at position " + i);
                    }
                    operators.push('~'); // Unary minus, highest precedence.
                } else {
                    // All binary operators are left-associative.
                    while (!operators.isEmpty() && operators.peek() != '('
                            && precedence(operators.peek()) >= precedence(c)) {
                        output.add(String.valueOf(operators.pop()));
                    }
                    operators.push(c);
                    expectOperand = true;
                }
                i++;
            } else {
                throw new IllegalArgumentException("Unexpected character '" + c + "' at position " + i);
            }
        }

        if (expectOperand) {
            throw new IllegalArgumentException("Expression ends without an operand");
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

    // Interpreter: build an expression tree from the postfix notation.
    private static Expression buildTree(List<String> postfix) {
        Deque<Expression> stack = new ArrayDeque<>();

        for (String token : postfix) {
            if (token.equals("~")) { // -x is evaluated as 0 - x.
                stack.push(new Minus(new Number(0), stack.pop()));
            } else if (token.length() == 1 && isBinaryOperator(token.charAt(0))) {
                Expression right = stack.pop();
                Expression left = stack.pop();
                switch (token.charAt(0)) {
                    case '+': stack.push(new Plus(left, right)); break;
                    case '-': stack.push(new Minus(left, right)); break;
                    case '*': stack.push(new Mul(left, right)); break;
                    case '/': stack.push(new Div(left, right)); break;
                    default: throw new IllegalStateException("Unexpected operator " + token);
                }
            } else {
                stack.push(new Number(Double.parseDouble(token)));
            }
        }
        return stack.pop();
    }

    private static String parseNumber(String number, int position) {
        try {
            Double.parseDouble(number);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number '" + number + "' at position " + position, e);
        }
        return number;
    }

    private static boolean isBinaryOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private static int precedence(char operator) {
        switch (operator) {
            case '~': return 3;
            case '*':
            case '/': return 2;
            case '+':
            case '-': return 1;
            default: return 0;
        }
    }
}
