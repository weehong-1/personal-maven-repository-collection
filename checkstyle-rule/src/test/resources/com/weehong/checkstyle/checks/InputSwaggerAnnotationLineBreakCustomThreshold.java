package com.weehong.checkstyle.checks;

/**
 * Test input for SwaggerAnnotationLineBreakCheck - custom threshold (3) cases.
 */
public class InputSwaggerAnnotationLineBreakCustomThreshold {

    @Schema(description = "Test", example = "value", requiredMode = RequiredMode.REQUIRED, maxLength = 100)
    private String fieldWithFourProperties;

    @Schema(description = "Test", example = "value", requiredMode = RequiredMode.REQUIRED)
    private String fieldWithThreeProperties;

    @Schema(description = "Test", example = "value")
    private String fieldWithTwoProperties;

    @Schema(
        description = "Test",
        example = "value",
        requiredMode = RequiredMode.REQUIRED,
        maxLength = 100
    )
    private String fieldWithFourPropertiesFormatted;

    private @interface Schema {
        String description() default "";
        String example() default "";
        RequiredMode requiredMode() default RequiredMode.AUTO;
        int maxLength() default Integer.MAX_VALUE;
    }

    private enum RequiredMode {
        AUTO, REQUIRED, NOT_REQUIRED
    }
}
