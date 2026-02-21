package com.weehong.checkstyle.checks;

public class InputSwitchSpacingCheckFallThrough {
    void methodWithFallThrough(int x) {

        switch (x) {
            case 1:
            case 2:
                System.out.println("one or two");
                break;

            default:
                System.out.println("other");
                break;
        }

    }
}
