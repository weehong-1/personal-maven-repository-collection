package com.weehong.checkstyle.checks;

/**
 * Test input for SwaggerAnnotationLineBreakCheck - valid cases.
 */
public class InputSwaggerAnnotationLineBreakValid {

    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "User email", example = "test@example.com")
    private String email;

    @Schema(
        description = "Auth0 user identifier",
        example = "auth0|123456789",
        requiredMode = RequiredMode.REQUIRED
    )
    private String auth0Id;

    @Operation(
        summary = "Get all users",
        description = "Retrieves a paginated list of all users. Requires ADMINISTRATOR role.",
        tags = {"users", "admin"}
    )
    public void getAllUsers() {
    }

    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions",
            content = @Content
        )
    })
    public void methodWithApiResponses() {
    }

    @Parameter(name = "id", description = "User ID")
    public void methodWithTwoProperties(String id) {
    }

    @Parameter(
        name = "id",
        description = "User ID",
        required = true
    )
    public void methodWithThreeProperties(String id) {
    }

    @RequestBody(
        description = "User creation request",
        required = true,
        content = @Content
    )
    public void methodWithRequestBody() {
    }

    @Header(
        name = "Authorization",
        description = "JWT Bearer token",
        required = true
    )
    public void methodWithHeader() {
    }

    @Schema
    private String simpleField;

    @Operation(summary = "Simple operation")
    public void simpleOperation() {
    }

    private @interface Schema {
        String description() default "";
        String example() default "";
        RequiredMode requiredMode() default RequiredMode.AUTO;
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
    }

    private @interface RequestBody {
        String description() default "";
        boolean required() default false;
        Content content() default @Content;
    }

    private @interface Header {
        String name();
        String description() default "";
        boolean required() default false;
    }
}
