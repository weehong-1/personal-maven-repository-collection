package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.jupiter.api.Test;

public class IfSpacingCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/weehong/checkstyle/checks";
    }

    @Test
    public void testIfSpacing() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(IfSpacingCheck.class);

        final String[] expected = {
            "6:9: Standalone if statement should be preceded by a blank line.",
            "6:9: Standalone if statement should be followed by a blank line.",
        };

        verify(checkConfig, getPath("InputIfSpacingCheck.java"), expected);
    }

    @Test
    public void testIfSpacingComprehensive() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(IfSpacingCheck.class);

        final String[] expected = {
            // Line 26: if in middle of block needs both preceding and following blank lines
            "26:9: Standalone if statement should be followed by a blank line.",
            "26:9: Standalone if statement should be preceded by a blank line.",
        };

        verify(checkConfig, getPath("InputIfSpacingCheckComprehensive.java"), expected);
    }
}
