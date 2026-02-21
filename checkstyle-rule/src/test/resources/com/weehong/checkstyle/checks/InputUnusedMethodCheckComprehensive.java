package com.weehong.checkstyle.checks;

import java.util.List;
import java.util.Map;

public class InputUnusedMethodCheckComprehensive {

    // Should not trigger (public method)
    public void publicMethod() {}

    // Should not trigger (special method - main)
    private static void main(String[] args) {}

    // Should not trigger (special method - serialization)
    private void writeObject(java.io.ObjectOutputStream out) {}
    private void readObject(java.io.ObjectInputStream in) {}
    private void readObjectNoData() {}
    private Object writeReplace() { return null; }
    private Object readResolve() { return null; }

    // Should not trigger (setUp/tearDown)
    private void setUp() {}
    private void setUpTest() {}
    private void tearDown() {}
    private void tearDownTest() {}

    // Unused private methods - SHOULD trigger violations
    private void unusedMethod() {}
    private int unusedWithPrimitives(int a, long b, boolean c, byte d, short e, char f, float g, double h) {
        return 0;
    }
    private void unusedWithArray(String[] arr, int[][] matrix) {}
    private void unusedWithGenerics(List<String> list, Map<String, Integer> map) {}
    private void unusedWithVarargs(String... args) {}

    // Used private methods - should NOT trigger violations
    private void usedMethod() {}
    private void usedViaDot() {}
    private void usedViaThis() {}

    public void caller() {
        usedMethod();
        this.usedViaThis();

        // Method reference usage
        Runnable r1 = this::usedViaDot;
    }

    // Inner class to test various call patterns
    class Inner {
        private void innerUnused() {}
        private void innerUsed() {}

        void call() {
            innerUsed();
        }
    }
}
