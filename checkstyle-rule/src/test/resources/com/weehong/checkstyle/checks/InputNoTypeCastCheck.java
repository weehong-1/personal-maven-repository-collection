package com.weehong.checkstyle.checks;

public class InputNoTypeCastCheck {
    public void method() {
        Object o = "string";
        String s = (String) o;
    }
}
