package com.weehong.checkstyle.checks;

public class InputNoHardcodedStringViolation {

    public void methodWithHardcodedStrings() {
        String role = "admin";

        if ("manager".equals(role)) {
            System.out.println("found");
        }
    }

    public String getStatus(boolean failed) {
        if (failed) {
            return "failure";
        }

        return "success";
    }

    public void methodWithMetrics() {
        String name = "app.method.execution";
    }
}
