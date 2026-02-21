package com.weehong.checkstyle.checks;

public class InputUnusedVariableCheckMainMethod {

    // Standard main method - args should not be flagged
    public static void main(String[] args) {
        // args is unused but this is the main method
        System.out.println("Hello");
    }

    // Non-public main method - args should be flagged
    private static void main2(String[] notMain) {
        // This is not actually a main method signature
    }

    // Non-static main method - args should be flagged
    public void main3(String[] notMain2) {
        // This is not actually a main method signature
    }

    // Main method without proper modifiers
    static void main4(String[] notMain3) {
        // Missing public modifier
    }

    // Regular method that happens to be named main
    public void main5(String[] notMain4) {
        // Not static, so not a real main method
    }

    // Method with Override annotation - parameter should not be flagged
    @Override
    public String toString() {
        return "test";
    }

    // Method with Override annotation - parameter should not be flagged
    @Override
    public boolean equals(Object obj) {
        return true;
    }

    // Method with multiple annotations including Override
    @Deprecated
    @Override
    public int hashCode() {
        return 42;
    }

    // Method with annotation but not Override - parameter should be flagged
    @Deprecated
    public void deprecatedMethod(String unusedParam) {
        // unusedParam should be flagged
    }
}
