package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.jupiter.api.Test;

class NoMultipleBlankLinesFileCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/weehong/checkstyle/checks";
    }

    @Test
    void testCorrectJavaFile() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoMultipleBlankLinesFileCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputNoMultipleBlankLinesCorrect.java"), expected);
    }

    @Test
    void testViolationJavaFile() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoMultipleBlankLinesFileCheck.class);

        final String[] expected = {
            "7: More than 1 consecutive blank line is not allowed.",
            "13: More than 1 consecutive blank line is not allowed.",
            "14: More than 1 consecutive blank line is not allowed.",
            "17: More than 1 consecutive blank line is not allowed.",
        };

        verify(checkConfig, getPath("InputNoMultipleBlankLinesViolation.java"), expected);
    }

    @Test
    void testCorrectXmlFile() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoMultipleBlankLinesFileCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputNoMultipleBlankLinesCorrect.xml"), expected);
    }

    @Test
    void testViolationXmlFile() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoMultipleBlankLinesFileCheck.class);

        final String[] expected = {
            "6: More than 1 consecutive blank line is not allowed.",
            "9: More than 1 consecutive blank line is not allowed.",
        };

        verify(checkConfig, getPath("InputNoMultipleBlankLinesViolation.xml"), expected);
    }
}
