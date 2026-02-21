package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.jupiter.api.Test;

class UnusedImportCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/weehong/checkstyle/checks";
    }

    @Test
    void testNoUnusedImports() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(UnusedImportCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputUnusedImportNoViolation.java"), expected);
    }

    @Test
    void testUnusedImport() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(UnusedImportCheck.class);

        final String[] expected = {
            "5:1: Unused import - 'java.util.HashMap'.",
            "6:1: Unused import - 'java.util.LinkedList'.",
        };

        verify(checkConfig, getPath("InputUnusedImportViolation.java"), expected);
    }

    @Test
    void testDuplicateImport() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(UnusedImportCheck.class);

        final String[] expected = {
            "3:1: Unused import - 'java.util.ArrayList'.",
            "5:1: Unused import - 'java.util.List'.",
            "6:1: Duplicate import 'java.util.List'.",
        };

        verify(checkConfig, getPath("InputUnusedImportDuplicate.java"), expected);
    }

    @Test
    void testStaticImport() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(UnusedImportCheck.class);

        final String[] expected = {
            "5:1: Unused import - 'java.util.Collections.emptyList'.",
        };

        verify(checkConfig, getPath("InputUnusedImportStatic.java"), expected);
    }

    @Test
    void testUsedInAnnotation() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(UnusedImportCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputUnusedImportAnnotation.java"), expected);
    }

}
