package com.weehong.checkstyle.checks;

import java.util.function.Function;

public class InputTernaryComprehensive {

    // Ternary in return statement - correct format
    public String returnTernaryCorrect(boolean condition) {
        return condition
            ? "yes"
            : "no";
    }

    // Ternary in return statement - violation on ? and :
    public String returnTernaryViolation(boolean condition) {
        return condition ? "yes" : "no";
    }

    // Ternary in method argument - correct format
    public void methodArgCorrect(boolean condition) {
        System.out.println(condition
            ? "yes"
            : "no");
    }

    // Ternary in method argument - violation
    public void methodArgViolation(boolean condition) {
        System.out.println(condition ? "yes" : "no");
    }

    // Ternary in lambda - correct format
    public void lambdaTernaryCorrect() {
        Function<Boolean, String> func = b -> b
            ? "yes"
            : "no";
    }

    // Ternary in lambda - violation
    public void lambdaTernaryViolation() {
        Function<Boolean, String> func = b -> b ? "yes" : "no";
    }

    // Nested ternary - correct format
    public String nestedTernaryCorrect(int x) {
        return x > 10
            ? "large"
            : x > 5
                ? "medium"
                : "small";
    }

    // Nested ternary - violations
    public String nestedTernaryViolation(int x) {
        return x > 10 ? "large" : x > 5 ? "medium" : "small";
    }

    // Multi-line condition - correct (? on new line)
    public String multiLineConditionCorrect(String s1, String s2) {
        return s1 != null
            && s2 != null
            ? s1 + s2
            : "null";
    }

    // Multi-line condition - violation (? on same line as condition end)
    public String multiLineConditionViolation(String s1, String s2) {
        return s1 != null
            && s2 != null ? s1 + s2
            : "null";
    }

    // Expression context - correct
    public void exprContextCorrect(boolean condition) {
        String result;
        result = condition
            ? "yes"
            : "no";
    }

    // Expression context - violation
    public void exprContextViolation(boolean condition) {
        String result;
        result = condition ? "yes" : "no";
    }
}
