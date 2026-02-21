package com.weehong.checkstyle.checks;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class InputUnusedMethodCheckAdvanced {

    // Method with qualified type name in parameter
    private void methodWithQualifiedType(java.util.Date date) {}

    // Method with double parameter
    private void methodWithDouble(double value) {}

    // Method with wildcard extends
    private void methodWithWildcardExtends(List<? extends Number> list) {}

    // Method with wildcard super
    private void methodWithWildcardSuper(List<? super Integer> list) {}

    // Method with nested generics
    private void methodWithNestedGenerics(Map<String, List<Integer>> map) {}

    // Method with array in generics
    private void methodWithArrayGeneric(List<String[]> list) {}

    // Used methods via different call patterns
    private void usedViaQualifiedThis() {}
    private void usedViaStaticRef() {}

    // This method tests qualified method calls (this.method())
    public void qualifiedCaller() {
        this.usedViaQualifiedThis();

        // Static method reference
        Consumer<String> consumer = System.out::println;
    }

    // Method reference via qualified expression
    private String processValue(String s) {
        return s.toUpperCase();
    }

    public void methodRefWithQualified() {
        Function<String, String> f = this::processValue;
    }

    // Inner class to test more complex scenarios
    class Inner {
        private void innerMethod() {}

        void useInner() {
            innerMethod();
        }
    }
}
