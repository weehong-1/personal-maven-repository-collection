package com.weehong.checkstyle.checks;

import java.io.InputStream;

// Test various logging annotations
@lombok.experimental.UtilityClass
public class InputNoForbiddenLombokComprehensive {

    // More logging annotations
    @Log
    @Log4j
    @Log4j2
    @CommonsLog
    @XSlf4j
    @Flogger
    @CustomLog
    @JBossLog
    static class LoggingTests {}

    // Constructor annotations
    @RequiredArgsConstructor
    static class RequiredArgsTest {
        private final String field;
    }

    // Other forbidden annotations
    @Accessors(chain = true)
    @FieldDefaults
    static class AccessorTests {
        private String field;
    }

    // Method level annotations
    static class MethodLevelTests {
        @SneakyThrows
        public void sneakyMethod() {}

        @Synchronized
        public void synchronizedMethod() {}

        @Tolerate
        public void tolerateMethod() {}
    }

    // Field level annotations
    static class FieldLevelTests {
        @With
        private String withField;

        @Delegate
        private Object delegate;

        @Singular
        private Object singular;
    }

    // Builder variations
    @SuperBuilder
    @Jacksonized
    static class BuilderVariations {
        private String field;
    }

    // Deeply qualified annotation (tests nested DOT handling)
    @lombok.Data
    @lombok.Builder
    static class DeeplyQualified {
        private String field;
    }
}
