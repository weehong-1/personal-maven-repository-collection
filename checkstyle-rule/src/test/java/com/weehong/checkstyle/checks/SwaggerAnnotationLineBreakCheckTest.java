package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.jupiter.api.Test;

public class SwaggerAnnotationLineBreakCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/weehong/checkstyle/checks";
    }

    @Test
    public void testSwaggerAnnotationLineBreakViolations() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(SwaggerAnnotationLineBreakCheck.class);

        final String[] expected = {
            "8:5: Swagger annotation @Schema with more than 2 properties "
                + "must have each property on a separate line.",
            "11:5: Swagger annotation @Operation with more than 2 properties "
                + "must have each property on a separate line.",
            "20:9: Swagger annotation @ApiResponse with more than 2 properties "
                + "must have each property on a separate line.",
            "31:5: Swagger annotation @Parameter with more than 2 properties "
                + "must have each property on a separate line.",
            "35:5: Swagger annotation @Schema with more than 2 properties "
                + "must have each property on a separate line.",
        };

        verify(checkConfig, getPath("InputSwaggerAnnotationLineBreakViolation.java"), expected);
    }

    @Test
    public void testSwaggerAnnotationLineBreakValid() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(SwaggerAnnotationLineBreakCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputSwaggerAnnotationLineBreakValid.java"), expected);
    }

    @Test
    public void testSwaggerAnnotationLineBreakWithCustomThreshold() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(SwaggerAnnotationLineBreakCheck.class);
        checkConfig.addProperty("maxPropertiesSameLine", "3");

        final String[] expected = {
            "8:5: Swagger annotation @Schema with more than 3 properties "
                + "must have each property on a separate line.",
        };

        verify(checkConfig, getPath("InputSwaggerAnnotationLineBreakCustomThreshold.java"), expected);
    }
}
