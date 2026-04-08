package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.jupiter.api.Test;

class NoHardcodedStringCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/weehong/checkstyle/checks";
    }

    @Test
    void testCorrectUsageWithConstants() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoHardcodedStringCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputNoHardcodedStringCorrect.java"), expected);
    }

    @Test
    void testHardcodedStringViolations() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoHardcodedStringCheck.class);

        final String[] expected = {
            "6:23: Avoid hardcoded string literals. Extract to a constant (static final) field.",
            "8:13: Avoid hardcoded string literals. Extract to a constant (static final) field.",
            "9:32: Avoid hardcoded string literals. Extract to a constant (static final) field.",
            "15:20: Avoid hardcoded string literals. Extract to a constant (static final) field.",
            "18:16: Avoid hardcoded string literals. Extract to a constant (static final) field.",
            "22:23: Avoid hardcoded string literals. Extract to a constant (static final) field.",
        };

        verify(checkConfig, getPath("InputNoHardcodedStringViolation.java"), expected);
    }

    @Test
    void testEnumConstantsAllowed() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoHardcodedStringCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputNoHardcodedStringEnum.java"), expected);
    }
}
