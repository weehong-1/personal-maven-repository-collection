package com.weehong.checkstyle.checks;

import lombok.*;

@Data
public class InputNoForbiddenLombokViolation {

    private String dataClass;

    @Builder
    public static class BuilderExample {
        private String field;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConstructorExample {
        private String field;
    }

    @Slf4j
    public static class LoggingExample {
        private String field;
    }

    @ToString
    @EqualsAndHashCode
    public static class ToStringExample {
        private String field;
    }

    @Value
    public static class ValueExample {
        String field;
    }

    public void methodWithNonNull(@NonNull String param) {
        String local = param;
    }

    public void methodWithCleanup() {
        @Cleanup InputStream in = null;
    }
}
