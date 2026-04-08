package com.weehong.checkstyle.checks;

public class InputNoHardcodedStringCorrect {

    private static final String ROLE_ADMIN = "admin";

    private static final String METRIC_NAME = "app.method.execution";

    private static final String STATUS = "success";

    public void methodUsingConstants() {
        String role = ROLE_ADMIN;

        if (ROLE_ADMIN.equals(role)) {
            System.out.println(STATUS);
        }

        String empty = "";
    }

    public void methodWithThrow() {
        throw new IllegalArgumentException("Invalid argument provided");
    }

    @SuppressWarnings("unchecked")
    public void methodWithAnnotation() {
    }
}
