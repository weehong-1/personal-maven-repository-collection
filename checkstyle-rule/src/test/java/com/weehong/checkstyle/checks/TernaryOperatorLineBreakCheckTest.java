package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.jupiter.api.Test;

class TernaryOperatorLineBreakCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/weehong/checkstyle/checks";
    }

    @Test
    void testCorrectTernaryFormat() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(TernaryOperatorLineBreakCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputTernaryCorrect.java"), expected);
    }

    @Test
    void testQuestionMarkOnSameLine() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(TernaryOperatorLineBreakCheck.class);

        final String[] expected = {
            "7:34: Ternary operator ? must be at the beginning of a new line.",
        };

        verify(checkConfig, getPath("InputTernaryQuestionSameLine.java"), expected);
    }

    @Test
    void testAllOnOneLine() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(TernaryOperatorLineBreakCheck.class);

        final String[] expected = {
            "7:34: Ternary operator ? must be at the beginning of a new line.",
            "7:42: Ternary operator : must be at the beginning of a new line.",
        };

        verify(checkConfig, getPath("InputTernaryAllOneLine.java"), expected);
    }

    @Test
    void testComprehensiveScenarios() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(TernaryOperatorLineBreakCheck.class);

        final String[] expected = {
            // return statement violation
            "16:26: Ternary operator ? must be at the beginning of a new line.",
            "16:34: Ternary operator : must be at the beginning of a new line.",
            // method argument violation
            "28:38: Ternary operator ? must be at the beginning of a new line.",
            "28:46: Ternary operator : must be at the beginning of a new line.",
            // lambda violation
            "40:49: Ternary operator ? must be at the beginning of a new line.",
            "40:57: Ternary operator : must be at the beginning of a new line.",
            // nested ternary violations
            "54:23: Ternary operator ? must be at the beginning of a new line.",
            "54:33: Ternary operator : must be at the beginning of a new line.",
            // multiline condition violation
            "68:27: Ternary operator ? must be at the beginning of a new line.",
            // expr context violation
            "83:28: Ternary operator ? must be at the beginning of a new line.",
            "83:36: Ternary operator : must be at the beginning of a new line.",
        };

        verify(checkConfig, getPath("InputTernaryComprehensive.java"), expected);
    }

}
