package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.jupiter.api.Test;

class NoMultipleBlankLinesCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/weehong/checkstyle/checks";
    }

    @Test
    void testCorrectSingleBlankLines() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoMultipleBlankLinesCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputNoMultipleBlankLinesCorrect.java"), expected);
    }

    @Test
    void testMultipleBlankLines() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoMultipleBlankLinesCheck.class);

        final String[] expected = {
            "7:1: More than 1 consecutive blank line is not allowed.",
            "13:1: More than 1 consecutive blank line is not allowed.",
            "14:1: More than 1 consecutive blank line is not allowed.",
            "17:1: More than 1 consecutive blank line is not allowed.",
        };

        verify(checkConfig, getPath("InputNoMultipleBlankLinesViolation.java"), expected);
    }
}
