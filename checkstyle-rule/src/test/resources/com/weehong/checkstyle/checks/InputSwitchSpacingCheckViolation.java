package com.weehong.checkstyle.checks;

public class InputSwitchSpacingCheckViolation {
    void method(int x) {
        int y = 1;
        switch (x) {
            default:
                break;
        }
        int z = 2;
    }
}
