package com.weehong.checkstyle.checks;

public class InputTernaryAllOneLine {

    public String getValue(boolean condition) {
        // violation: all on one line
        String value = condition ? "yes" : "no";
        return value;
    }

}
