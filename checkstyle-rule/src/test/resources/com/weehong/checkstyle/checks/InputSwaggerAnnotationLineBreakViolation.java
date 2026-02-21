package com.weehong.checkstyle.checks;

/**
 * Test input for SwaggerAnnotationLineBreakCheck - violation cases.
 */
public class InputSwaggerAnnotationLineBreakViolation {

    @Schema(description = "Auth0 user identifier", example = "auth0|123456789", requiredMode = RequiredMode.REQUIRED)
    private String userId;

    @Operation(summary = "Get all users", description = "Retrieves all users", tags = {"users", "admin"})
    public void getAllUsers() {
    }

    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Success"
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public void methodWithApiResponses() {
    }

    @Parameter(
        name = "id",
        description = "User ID",
        required = true,
        example = "123"
    )
    @Parameter(name = "id", description = "User ID", required = true, example = "123")
    public void methodWithParameter(String id) {
    }

    @Schema(description = "Test field", example = "value", requiredMode = RequiredMode.REQUIRED,
            maxLength = 100)
    private String testField;

    private @interface Schema {
        String description() default "";
        String example() default "";
        RequiredMode requiredMode() default RequiredMode.AUTO;
        int maxLength() default Integer.MAX_VALUE;
    }

    private enum RequiredMode {
        AUTO, REQUIRED, NOT_REQUIRED
    }

    private @interface Operation {
        String summary() default "";
        String description() default "";
        String[] tags() default {};
    }

    private @interface ApiResponses {
        ApiResponse[] value();
    }

    private @interface ApiResponse {
        String responseCode();
        String description() default "";
        Content content() default @Content;
    }

    private @interface Content {
    }

    private @interface Parameter {
        String name();
        String description() default "";
        boolean required() default false;
        String example() default "";
    }
}
