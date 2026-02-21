package com.weehong.checkstyle.checks;

public class InputTernaryQuestionSameLine {

    public String getValue(boolean condition) {
        // violation: ? on same line as condition
        String value = condition ? "yes"
            : "no";
        return value;
    }

}
