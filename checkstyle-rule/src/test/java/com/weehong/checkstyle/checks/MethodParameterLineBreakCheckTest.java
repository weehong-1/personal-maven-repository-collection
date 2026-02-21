package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.jupiter.api.Test;

public class MethodParameterLineBreakCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/weehong/checkstyle/checks";
    }

    @Test
    public void testMethodParameterLineBreak() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(MethodParameterLineBreakCheck.class);

        final String[] expected = {
            "5:5: Method with more than 4 parameters must have each parameter on a separate line.",
        };

        verify(checkConfig, getPath("InputMethodParameterLineBreakCheck.java"), expected);
    }
}
