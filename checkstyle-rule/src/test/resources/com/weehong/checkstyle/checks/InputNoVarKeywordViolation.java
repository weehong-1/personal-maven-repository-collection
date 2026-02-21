package com.weehong.checkstyle.checks;

import java.util.ArrayList;

public class InputNoVarKeywordViolation {

    public void methodWithVarKeyword() {
        var name = "John";

        var age = 25;

        var list = new ArrayList<String>();

        if (true) {
            var localVar = "test";
        }

        try {
            var result = calculate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int calculate() {
        return 42;
    }
}
