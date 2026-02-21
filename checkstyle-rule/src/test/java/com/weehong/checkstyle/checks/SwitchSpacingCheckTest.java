package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.jupiter.api.Test;

class SwitchSpacingCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/weehong/checkstyle/checks";
    }

    @Test
    void testSwitchSpacingCorrect() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(SwitchSpacingCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputSwitchSpacingCheckCorrect.java"), expected);
    }

    @Test
    void testSwitchSpacingViolations() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(SwitchSpacingCheck.class);

        final String[] expected = {
            "6:9: Standalone switch statement should be preceded by a blank line.",
            "6:9: Standalone switch statement should be followed by a blank line.",
        };

        verify(checkConfig, getPath("InputSwitchSpacingCheckViolation.java"), expected);
    }

    @Test
    void testSwitchSpacingWithCaseViolations() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(SwitchSpacingCheck.class);

        // Cases without blank lines between them should trigger violations
        final String[] expected = {
            "9:17: First case group should be followed by a blank line.",
            "10:13: Case group should be preceded by a blank line.",
        };

        verify(checkConfig, getPath("InputSwitchSpacingCheckWithCases.java"), expected);
    }

    @Test
    void testSwitchSpacingSingleCase() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(SwitchSpacingCheck.class);

        // Single case should have no case spacing violations
        final String[] expected = {};

        verify(checkConfig, getPath("InputSwitchSpacingCheckSingleCase.java"), expected);
    }

    @Test
    void testSwitchSpacingMiddleCaseViolations() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(SwitchSpacingCheck.class);

        // Three cases without blank lines - tests first, middle, and last case violations
        final String[] expected = {
            "9:17: First case group should be followed by a blank line.",
            "10:13: Case group should be preceded by a blank line.",
            "12:17: Middle case group should be followed by a blank line.",
            "13:13: Case group should be preceded by a blank line.",
        };

        verify(checkConfig, getPath("InputSwitchSpacingCheckMiddleViolation.java"), expected);
    }

    @Test
    void testSwitchSpacingEmptySwitch() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(SwitchSpacingCheck.class);

        // Empty switch (no case groups) should have no case spacing violations
        final String[] expected = {};

        verify(checkConfig, getPath("InputSwitchSpacingCheckEmpty.java"), expected);
    }

    @Test
    void testSwitchSpacingFallThrough() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(SwitchSpacingCheck.class);

        // Fall-through cases with proper spacing should have no violations
        final String[] expected = {};

        verify(checkConfig, getPath("InputSwitchSpacingCheckFallThrough.java"), expected);
    }

    @Test
    void testSwitchSpacingWithComments() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(SwitchSpacingCheck.class);

        // Switch with comments between case groups and empty case bodies
        final String[] expected = {};

        verify(checkConfig, getPath("InputSwitchSpacingCheckWithComments.java"), expected);
    }

    @Test
    void testGetRequiredTokens() {
        final SwitchSpacingCheck check = new SwitchSpacingCheck();
        int[] tokens = check.getRequiredTokens();
        // Verify it returns LITERAL_SWITCH token
        assert tokens.length == 1;
    }
}
