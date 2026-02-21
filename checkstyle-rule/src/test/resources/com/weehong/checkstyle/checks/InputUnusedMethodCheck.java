package com.weehong.checkstyle.checks;

public class InputUnusedMethodCheck {

    private void unusedPrivateMethod() {
    }

    private void usedPrivateMethod() {
    }

    public void publicMethod() {
        usedPrivateMethod();
    }

    private void unusedWithParams(int a, String b) {
    }
    
    private void usedViaMethodRef() {
    }
    
    public void methodRefCaller() {
        Runnable r = this::usedViaMethodRef;
    }
}
