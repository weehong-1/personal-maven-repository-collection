package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.jupiter.api.Test;

public class WhileSpacingCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/weehong/checkstyle/checks";
    }

    @Test
    public void testWhileSpacing() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(WhileSpacingCheck.class);

        final String[] expected = {
            "6:9: Standalone while statement should be preceded by a blank line.",
            "6:9: Standalone while statement should be followed by a blank line.",
        };

        verify(checkConfig, getPath("InputWhileSpacingCheck.java"), expected);
    }
}
