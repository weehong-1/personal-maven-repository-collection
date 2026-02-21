package com.weehong.checkstyle.checks;

public class InputTernaryCorrect {

    public String getValue(boolean condition) {
        String value = condition
            ? "yes"
            : "no";
        return value;
    }

}
