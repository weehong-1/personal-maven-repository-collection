package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.jupiter.api.Test;

class NoVarKeywordCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/weehong/checkstyle/checks";
    }

    @Test
    void testCorrectExplicitTypes() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoVarKeywordCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputNoVarKeywordCorrect.java"), expected);
    }

    @Test
    void testVarKeywordViolations() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoVarKeywordCheck.class);

        final String[] expected = {
            "8:9: Use of 'var' keyword is not allowed. Concrete data type must be explicitly declared.",
            "10:9: Use of 'var' keyword is not allowed. Concrete data type must be explicitly declared.",
            "12:9: Use of 'var' keyword is not allowed. Concrete data type must be explicitly declared.",
            "15:13: Use of 'var' keyword is not allowed. Concrete data type must be explicitly declared.",
            "19:13: Use of 'var' keyword is not allowed. Concrete data type must be explicitly declared.",
        };

        verify(checkConfig, getPath("InputNoVarKeywordViolation.java"), expected);
    }

    @Test
    void testVarKeywordInForLoop() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoVarKeywordCheck.class);

        final String[] expected = {
            "8:14: Use of 'var' keyword is not allowed. Concrete data type must be explicitly declared.",
        };

        verify(checkConfig, getPath("InputNoVarKeywordForLoop.java"), expected);
    }
}
