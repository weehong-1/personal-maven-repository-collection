package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.jupiter.api.Test;

public class NoTypeCastCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/weehong/checkstyle/checks";
    }

    @Test
    public void testTypeCast() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(NoTypeCastCheck.class);

        final String[] expected = {
            "6:20: Avoid using type casting. Consider using generics, polymorphism, or instanceof pattern matching instead.",
        };

        verify(checkConfig, getPath("InputNoTypeCastCheck.java"), expected);
    }
}
