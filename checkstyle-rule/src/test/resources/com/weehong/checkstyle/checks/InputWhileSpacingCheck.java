package com.weehong.checkstyle.checks;

public class InputWhileSpacingCheck {
    void method() {
        int i = 0;
        while (i < 10) { i++; }
        int j = 0;
        
        do { j++; } while (j < 10);
    }
}
