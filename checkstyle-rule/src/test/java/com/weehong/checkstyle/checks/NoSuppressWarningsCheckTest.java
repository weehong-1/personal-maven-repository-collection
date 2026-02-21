package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.jupiter.api.Test;

class NoSuppressWarningsCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/weehong/checkstyle/checks";
    }

    @Test
    void testNoViolations() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoSuppressWarningsCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputNoSuppressWarningsCorrect.java"), expected);
    }

    @Test
    void testSuppressWarningsViolations() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoSuppressWarningsCheck.class);

        final String[] expected = {
            "5:1: Avoid using @SuppressWarnings annotation. Fix the underlying issue instead of suppressing the warning.",
            "8:5: Avoid using @SuppressWarnings annotation. Fix the underlying issue instead of suppressing the warning.",
            "11:5: Avoid using @SuppressWarnings annotation. Fix the underlying issue instead of suppressing the warning.",
        };

        verify(checkConfig, getPath("InputNoSuppressWarningsViolation.java"), expected);
    }
}
