package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.jupiter.api.Test;

class UnusedMethodCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/weehong/checkstyle/checks";
    }

    @Test
    void testUnusedMethods() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(UnusedMethodCheck.class);

        final String[] expected = {
            "5:5: Private method 'unusedPrivateMethod()' is declared but never used.",
            "15:5: Private method 'unusedWithParams(int,String)' is declared but never used.",
        };

        verify(checkConfig, getPath("InputUnusedMethodCheck.java"), expected);
    }

    @Test
    void testComprehensiveScenarios() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(UnusedMethodCheck.class);

        final String[] expected = {
            "28:5: Private method 'unusedMethod()' is declared but never used.",
            "29:5: Private method 'unusedWithPrimitives(int,long,boolean,byte,short,char,float,double)' is declared but never used.",
            "32:5: Private method 'unusedWithArray(String[],int[][])' is declared but never used.",
            "33:5: Private method 'unusedWithGenerics(List<String>,Map<String,Integer>)' is declared but never used.",
            "34:5: Private method 'unusedWithVarargs(String...)' is declared but never used.",
            "51:9: Private method 'innerUnused()' is declared but never used.",
        };

        verify(checkConfig, getPath("InputUnusedMethodCheckComprehensive.java"), expected);
    }

    @Test
    void testFrameworkAnnotations() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(UnusedMethodCheck.class);

        // All methods have framework annotations, so no violations expected
        final String[] expected = {};

        verify(checkConfig, getPath("InputUnusedMethodCheckAnnotations.java"), expected);
    }

    @Test
    void testAdvancedTypeScenarios() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(UnusedMethodCheck.class);

        // Test advanced type scenarios: qualified types, wildcards, nested generics
        // Note: wildcard bounds (extends/super) are simplified to just '?'
        final String[] expected = {
            "11:5: Private method 'methodWithQualifiedType(java.util.Date)' is declared but never used.",
            "14:5: Private method 'methodWithDouble(double)' is declared but never used.",
            "17:5: Private method 'methodWithWildcardExtends(List<?>)' is declared but never used.",
            "20:5: Private method 'methodWithWildcardSuper(List<?>)' is declared but never used.",
            "23:5: Private method 'methodWithNestedGenerics(Map<String,List<Integer>>)' is declared but never used.",
            "26:5: Private method 'methodWithArrayGeneric(List<String[]>)' is declared but never used.",
            "30:5: Private method 'usedViaStaticRef()' is declared but never used.",
        };

        verify(checkConfig, getPath("InputUnusedMethodCheckAdvanced.java"), expected);
    }

    @Test
    void testMethodReferences() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(UnusedMethodCheck.class);

        // Test method references with this::methodName
        final String[] expected = {
            "21:5: Private method 'unusedMethodRef()' is declared but never used.",
        };

        verify(checkConfig, getPath("InputUnusedMethodCheckMethodRefs.java"), expected);
    }

    @Test
    void testGetAcceptableTokens() {
        final UnusedMethodCheck check = new UnusedMethodCheck();
        int[] tokens = check.getAcceptableTokens();
        // Verify tokens are returned
        assert tokens.length == 3;
    }

    @Test
    void testGetDefaultTokens() {
        final UnusedMethodCheck check = new UnusedMethodCheck();
        int[] tokens = check.getDefaultTokens();
        // Verify tokens are returned
        assert tokens.length == 3;
    }

    @Test
    void testMethodReferencesWithDot() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(UnusedMethodCheck.class);

        // Test method references with DOT structure (ClassName::method, instance::method)
        // Note: Current implementation doesn't detect ClassName::method or instance::method patterns
        // Only this::method is properly detected
        final String[] expected = {
            "11:5: Private method 'staticHelper(String)' is declared but never used.",
            "16:5: Private method 'unusedStatic()' is declared but never used.",
            "21:5: Private method 'instanceHelper(String)' is declared but never used.",
            "26:5: Private method 'unusedInstance()' is declared but never used.",
            "43:9: Private method 'innerUnused()' is declared but never used.",
        };

        verify(checkConfig, getPath("InputUnusedMethodCheckMethodRefsDot.java"), expected);
    }
}
