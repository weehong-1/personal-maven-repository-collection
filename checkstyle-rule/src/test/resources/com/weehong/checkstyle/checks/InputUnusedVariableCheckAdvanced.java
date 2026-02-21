package com.weehong.checkstyle.checks;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class InputUnusedVariableCheckAdvanced {

    // Constructor with unused parameter
    public InputUnusedVariableCheckAdvanced(String unusedCtorParam) {
        // not using unusedCtorParam
    }

    // Constructor with used parameter
    public InputUnusedVariableCheckAdvanced(int usedParam, String alsoUsed) {
        System.out.println(usedParam + alsoUsed);
    }

    // For-each loop with used variable
    public void forEachUsed(List<String> items) {
        for (String item : items) {
            System.out.println(item);
        }
    }

    // Method with @Override - parameters should not be flagged
    @Override
    public String toString() {
        return "test";
    }

    @Override
    public boolean equals(Object obj) {
        // obj is unused but this is an override method
        return true;
    }

    // Serialization methods - parameters should not be flagged
    private void writeObject(ObjectOutputStream out) {
        // out is unused but this is a serialization method
    }

    private void readObject(ObjectInputStream in) {
        // in is unused but this is a serialization method
    }

    private void readObjectNoData() {
        // no params but still a serialization method
    }

    private Object writeReplace() {
        return this;
    }

    private Object readResolve() {
        return this;
    }

    // Nested class to test nested method handling
    class Inner {
        void innerMethod(int param) {
            System.out.println(param);
        }

        void innerWithUnused(int unused) {
            // unused parameter in inner class method
        }
    }
}
