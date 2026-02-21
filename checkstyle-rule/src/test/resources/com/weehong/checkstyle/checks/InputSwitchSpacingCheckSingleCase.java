package com.weehong.checkstyle.checks;

public class InputSwitchSpacingCheckSingleCase {
    void method(int x) {

        switch (x) {
            default:
                System.out.println("default");
                break;
        }

    }
}
