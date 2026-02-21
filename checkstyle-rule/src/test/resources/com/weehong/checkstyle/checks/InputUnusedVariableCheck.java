package com.weehong.checkstyle.checks;

import java.util.Collections;
import java.util.List;

public class InputUnusedVariableCheck {

    public void method(int usedParam, int unusedParam) {
        int unusedLocal = 10;
        int usedLocal = 20;
        System.out.println(usedLocal);
        System.out.println(usedParam);
    }

    public static void main(String[] args) {
        int usedInMain = 1;
        System.out.println(usedInMain);
    }

    public void loops() {
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
        }
        List<String> list = Collections.emptyList();
        for (String unusedLoopVar : list) {
             // unused
        }
    }

    public void catches() {
        try {
            method(1, 2);
        } catch (Exception e) {
            // catch parameter should be ignored
        }
    }
    
    @Override
    public String toString() {
        // method with override, usually no params, but if it had... 
        return "";
    }
    
    public void testOverride(int p) {
        // normal method
    }
}
