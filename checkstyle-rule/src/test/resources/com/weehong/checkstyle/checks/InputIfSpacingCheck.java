package com.weehong.checkstyle.checks;

public class InputIfSpacingCheck {
    void method() {
        int i = 0;
        if (true) {}
        int j = 0;
        
        if (true) {} else {}
    }
}
