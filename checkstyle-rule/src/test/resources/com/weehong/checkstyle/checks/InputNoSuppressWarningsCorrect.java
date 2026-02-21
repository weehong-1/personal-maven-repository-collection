package com.weehong.checkstyle.checks;

public class InputNoSuppressWarningsCorrect {

    @Override
    public String toString() {
        return "No SuppressWarnings used";
    }

    @Deprecated
    public void deprecatedMethod() {
    }
}
