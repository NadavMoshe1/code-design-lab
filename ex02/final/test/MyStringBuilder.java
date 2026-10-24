package test;

import java.util.ArrayList;
import java.util.Collections;
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
    // null is appended as "null", like java.lang.StringBuilder
    public MyStringBuilder append(String str) {
        if (str == null) {
            str = "null";
        }

        for (char c : str.toCharArray()) {
            buff.add(c);
        }

        return this;
    }

    // implement reverse
    public MyStringBuilder reverse() {
        Collections.reverse(buff);
        return this;
    }

    // implement apply
    // ArrayList.replaceAll already takes a UnaryOperator and throws NullPointerException on null
    public MyStringBuilder apply(UnaryOperator<Character> function) {
        buff.replaceAll(function);
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
