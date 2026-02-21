package com.weehong.checkstyle.checks;

public class InputMethodParameterLineBreakCheck {
    // 5 parameters on one line - violation
    void method1(int a, int b, int c, int d, int e) {}

    // 5 parameters on separate lines - ok
    void method2(
        int a,
        int b,
        int c,
        int d,
        int e
    ) {}
    
    // 4 parameters on one line - ok
    void method3(int a, int b, int c, int d) {}
}
