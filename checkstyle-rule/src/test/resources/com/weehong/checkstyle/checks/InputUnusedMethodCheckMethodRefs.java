package com.weehong.checkstyle.checks;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class InputUnusedMethodCheckMethodRefs {

    // Method reference used with qualified this
    private void usedViaQualifiedRef() {
        System.out.println("used");
    }

    // Method reference used with DOT expression
    private String transformValue(String s) {
        return s.toUpperCase();
    }

    // Unused method to trigger violation
    private void unusedMethodRef() {
        System.out.println("unused");
    }

    public void caller() {
        // Method ref via simple this::
        Consumer<Void> c = v -> usedViaQualifiedRef();

        // Method ref via DOT
        Function<String, String> f = this::transformValue;

        // Using the function
        String result = f.apply("test");
        System.out.println(result);
    }

    // Static method reference usage
    public void useStaticRef() {
        List<String> items = Arrays.asList("a", "b", "c");
        items.forEach(System.out::println);
    }
}
