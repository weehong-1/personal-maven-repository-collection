package com.weehong.checkstyle.checks;

public class InputSwitchSpacingCheckMiddleViolation {
    void methodWithThreeCases(int x) {

        switch (x) {
            case 1:
                System.out.println("one");
                break;
            case 2:
                System.out.println("two");
                break;
            case 3:
                System.out.println("three");
                break;
        }

    }
}
