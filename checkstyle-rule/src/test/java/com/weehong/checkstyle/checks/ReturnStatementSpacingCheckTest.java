package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.jupiter.api.Test;

public class ReturnStatementSpacingCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/weehong/checkstyle/checks";
    }

    @Test
    public void testReturnStatementSpacing() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ReturnStatementSpacingCheck.class);

        final String[] expected = {
            "19:9: return statement should be preceded by a blank line.",
            "39:9: return statement should be preceded by a blank line.",
            "56:9: return statement should be preceded by a blank line.",
            "71:9: return statement should be preceded by a blank line.",
        };

        verify(checkConfig, getPath("InputReturnStatementSpacingCheck.java"), expected);
    }
}
