
package test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class MyStringBuilder {
    // ---------- DO NOT Change ----------------
    ArrayList<Character> buff;

    public MyStringBuilder() {
        buff=new ArrayList<>();
    }

    public MyStringBuilder(char...ts) {
        buff=new ArrayList<>();
        for(char t : ts)
            buff.add(t);
    }

    // -----------------------------------------

    // implement append
    public MyStringBuilder append(String str) {
        if (str == null) {
            str = "null";
        }

        for (int i = 0; i < str.length(); i++) {
            buff.add(str.charAt(i));
        }

        return this;
    }

    // implement reverse
    public MyStringBuilder reverse() {
        int left = 0;
        int right = buff.size() - 1;

        while (left < right) {
            char temp = buff.get(left);
            buff.set(left, buff.get(right));
            buff.set(right, temp);

            left++;
            right--;
        }

        return this;
    }

    // implement apply
    public MyStringBuilder apply(UnaryOperator<Character> function) {
        if (function == null) {
            throw new NullPointerException();
        }

        for (int i = 0; i < buff.size(); i++) {
            buff.set(i, function.apply(buff.get(i)));
        }

        return this;
    }

    // implement toString
    @Override
    public String toString() {
        char[] result = new char[buff.size()];

        for (int i = 0; i < buff.size(); i++) {
            result[i] = buff.get(i);
        }

        return new String(result);
    }
}
