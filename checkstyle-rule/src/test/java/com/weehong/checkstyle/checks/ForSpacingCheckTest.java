package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.jupiter.api.Test;

public class ForSpacingCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/weehong/checkstyle/checks";
    }

    @Test
    public void testForSpacing() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForSpacingCheck.class);

        final String[] expected = {
            "6:9: Standalone for statement should be preceded by a blank line.",
            "6:9: Standalone for statement should be followed by a blank line.",
        };

        verify(checkConfig, getPath("InputForSpacingCheck.java"), expected);
    }
}
