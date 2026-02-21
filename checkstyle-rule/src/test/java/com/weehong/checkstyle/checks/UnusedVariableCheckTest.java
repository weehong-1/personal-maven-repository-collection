package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.jupiter.api.Test;

public class UnusedVariableCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/weehong/checkstyle/checks";
    }

    @Test
    public void testUnusedVariables() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(UnusedVariableCheck.class);

        final String[] expected = {
            "8:39: Parameter 'unusedParam' is declared but never used.",
            "9:9: Local variable 'unusedLocal' is declared but never used.",
            "25:14: Local variable 'unusedLoopVar' is declared but never used.",
            "44:30: Parameter 'p' is declared but never used.",
        };

        verify(checkConfig, getPath("InputUnusedVariableCheck.java"), expected);
    }

    @Test
    public void testAdvancedVariableScenarios() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(UnusedVariableCheck.class);

        // Constructor unused param and inner class unused param
        final String[] expected = {
            "10:45: Parameter 'unusedCtorParam' is declared but never used.",
            "65:30: Parameter 'unused' is declared but never used.",
        };

        verify(checkConfig, getPath("InputUnusedVariableCheckAdvanced.java"), expected);
    }

    @Test
    public void testMainMethodVariants() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(UnusedVariableCheck.class);

        // Tests main method detection: only public static void main(String[]) should be ignored
        // Non-public, non-static, or differently named main methods should flag unused params
        final String[] expected = {
            "12:31: Parameter 'notMain' is declared but never used.",
            "17:23: Parameter 'notMain2' is declared but never used.",
            "22:23: Parameter 'notMain3' is declared but never used.",
            "27:23: Parameter 'notMain4' is declared but never used.",
            "52:34: Parameter 'unusedParam' is declared but never used.",
        };

        verify(checkConfig, getPath("InputUnusedVariableCheckMainMethod.java"), expected);
    }

    @Test
    public void testGetAcceptableTokens() {
        final UnusedVariableCheck check = new UnusedVariableCheck();
        int[] tokens = check.getAcceptableTokens();
        // Verify tokens are returned
        assert tokens.length == 5;
    }

    @Test
    public void testGetDefaultTokens() {
        final UnusedVariableCheck check = new UnusedVariableCheck();
        int[] tokens = check.getDefaultTokens();
        // Verify tokens are returned
        assert tokens.length == 5;
    }
}