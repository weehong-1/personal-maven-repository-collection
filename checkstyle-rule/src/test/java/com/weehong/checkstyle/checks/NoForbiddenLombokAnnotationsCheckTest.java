package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.jupiter.api.Test;

class NoForbiddenLombokAnnotationsCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/weehong/checkstyle/checks";
    }

    @Test
    void testAllowedAnnotations() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoForbiddenLombokAnnotationsCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputNoForbiddenLombokAllowed.java"), expected);
    }

    @Test
    void testForbiddenAnnotations() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoForbiddenLombokAnnotationsCheck.class);

        final String[] expected = {
            "5:1: Lombok annotation @Data is not allowed. Only @Getter and @Setter are permitted.",
            "10:5: Lombok annotation @Builder is not allowed. Only @Getter and @Setter are permitted.",
            "15:5: Lombok annotation @AllArgsConstructor is not allowed. Only @Getter and @Setter are permitted.",
            "16:5: Lombok annotation @NoArgsConstructor is not allowed. Only @Getter and @Setter are permitted.",
            "21:5: Lombok annotation @Slf4j is not allowed. Only @Getter and @Setter are permitted.",
            "26:5: Lombok annotation @ToString is not allowed. Only @Getter and @Setter are permitted.",
            "27:5: Lombok annotation @EqualsAndHashCode is not allowed. Only @Getter and @Setter are permitted.",
            "32:5: Lombok annotation @Value is not allowed. Only @Getter and @Setter are permitted.",
            "37:35: Lombok annotation @NonNull is not allowed. Only @Getter and @Setter are permitted.",
            "42:9: Lombok annotation @Cleanup is not allowed. Only @Getter and @Setter are permitted.",
        };

        verify(checkConfig, getPath("InputNoForbiddenLombokViolation.java"), expected);
    }

    @Test
    void testMixedAnnotations() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoForbiddenLombokAnnotationsCheck.class);

        final String[] expected = {
            "7:1: Lombok annotation @Builder is not allowed. Only @Getter and @Setter are permitted.",
        };

        verify(checkConfig, getPath("InputNoForbiddenLombokMixed.java"), expected);
    }

    @Test
    void testQualifiedAnnotations() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoForbiddenLombokAnnotationsCheck.class);

        final String[] expected = {
            "3:1: Lombok annotation @Data is not allowed. Only @Getter and @Setter are permitted.",
            "8:5: Lombok annotation @Builder is not allowed. Only @Getter and @Setter are permitted.",
        };

        verify(checkConfig, getPath("InputNoForbiddenLombokQualified.java"), expected);
    }

    @Test
    void testComprehensiveAnnotations() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoForbiddenLombokAnnotationsCheck.class);

        final String[] expected = {
            "6:1: Lombok annotation @UtilityClass is not allowed. Only @Getter and @Setter are permitted.",
            "10:5: Lombok annotation @Log is not allowed. Only @Getter and @Setter are permitted.",
            "11:5: Lombok annotation @Log4j is not allowed. Only @Getter and @Setter are permitted.",
            "12:5: Lombok annotation @Log4j2 is not allowed. Only @Getter and @Setter are permitted.",
            "13:5: Lombok annotation @CommonsLog is not allowed. Only @Getter and @Setter are permitted.",
            "14:5: Lombok annotation @XSlf4j is not allowed. Only @Getter and @Setter are permitted.",
            "15:5: Lombok annotation @Flogger is not allowed. Only @Getter and @Setter are permitted.",
            "16:5: Lombok annotation @CustomLog is not allowed. Only @Getter and @Setter are permitted.",
            "17:5: Lombok annotation @JBossLog is not allowed. Only @Getter and @Setter are permitted.",
            "21:5: Lombok annotation @RequiredArgsConstructor is not allowed. Only @Getter and @Setter are permitted.",
            "27:5: Lombok annotation @Accessors is not allowed. Only @Getter and @Setter are permitted.",
            "28:5: Lombok annotation @FieldDefaults is not allowed. Only @Getter and @Setter are permitted.",
            "35:9: Lombok annotation @SneakyThrows is not allowed. Only @Getter and @Setter are permitted.",
            "38:9: Lombok annotation @Synchronized is not allowed. Only @Getter and @Setter are permitted.",
            "41:9: Lombok annotation @Tolerate is not allowed. Only @Getter and @Setter are permitted.",
            "47:9: Lombok annotation @With is not allowed. Only @Getter and @Setter are permitted.",
            "50:9: Lombok annotation @Delegate is not allowed. Only @Getter and @Setter are permitted.",
            "53:9: Lombok annotation @Singular is not allowed. Only @Getter and @Setter are permitted.",
            "58:5: Lombok annotation @SuperBuilder is not allowed. Only @Getter and @Setter are permitted.",
            "59:5: Lombok annotation @Jacksonized is not allowed. Only @Getter and @Setter are permitted.",
            "65:5: Lombok annotation @Data is not allowed. Only @Getter and @Setter are permitted.",
            "66:5: Lombok annotation @Builder is not allowed. Only @Getter and @Setter are permitted.",
        };

        verify(checkConfig, getPath("InputNoForbiddenLombokComprehensive.java"), expected);
    }
}
