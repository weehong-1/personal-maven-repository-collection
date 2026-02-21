package com.weehong.checkstyle.checks;

public class InputSwitchSpacingCheckCorrect {
    void methodWithCorrectSpacing(int x) {

        switch (x) {
            case 1:
                System.out.println("one");
                break;

            case 2:
                System.out.println("two");
                break;

            default:
                System.out.println("other");
                break;
        }

    }

    void methodWithSingleCase(int x) {
        switch (x) {
            default:
                break;
        }
    }
}
