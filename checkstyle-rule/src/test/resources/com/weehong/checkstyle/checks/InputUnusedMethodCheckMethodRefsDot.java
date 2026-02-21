package com.weehong.checkstyle.checks;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class InputUnusedMethodCheckMethodRefsDot {

    // Used via static method reference with dot
    private static String staticHelper(String s) {
        return s.trim();
    }

    // Unused static method - should be flagged
    private static void unusedStatic() {
        System.out.println("unused");
    }

    // Used via instance method reference
    private String instanceHelper(String s) {
        return s.toLowerCase();
    }

    // Unused instance method - should be flagged
    private void unusedInstance() {
        System.out.println("unused");
    }

    public void testMethodRefs() {
        List<String> items = Arrays.asList("A", "B", "C");

        // Static method reference: ClassName::method
        items.stream().map(InputUnusedMethodCheckMethodRefsDot::staticHelper);

        // Instance method reference via instance: instance::method
        InputUnusedMethodCheckMethodRefsDot instance = new InputUnusedMethodCheckMethodRefsDot();
        items.stream().map(instance::instanceHelper);
    }

    // Inner class with method references
    class Inner {
        private void innerUnused() {
            System.out.println("inner unused");
        }

        private String innerUsed(String s) {
            return s;
        }

        void useInnerMethod() {
            Function<String, String> f = this::innerUsed;
            f.apply("test");
        }
    }
}
