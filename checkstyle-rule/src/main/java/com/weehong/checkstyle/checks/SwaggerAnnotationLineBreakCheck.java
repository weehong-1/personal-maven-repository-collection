package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.StatelessCheck;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Checkstyle check that enforces OpenAPI/Swagger annotations to have each property
 * on a separate line when the annotation has more than a configurable threshold
 * of properties (default: 2).
 *
 * <p>This check targets the following annotations:
 * <ul>
 *   <li>@Schema</li>
 *   <li>@Operation</li>
 *   <li>@ApiResponse</li>
 *   <li>@ApiResponses</li>
 *   <li>@Parameter</li>
 *   <li>@RequestBody</li>
 *   <li>@Header</li>
 *   <li>@Content</li>
 * </ul>
 *
 * <p>Example of compliant code (2 or fewer properties):
 * <pre>
 * &#64;Schema(description = "User ID", example = "123")
 * </pre>
 *
 * <p>Example of compliant code (more than 2 properties, properly formatted):
 * <pre>
 * &#64;Schema(
 *     description = "Auth0 user identifier",
 *     example = "auth0|123456789",
 *     requiredMode = Schema.RequiredMode.REQUIRED
 * )
 * </pre>
 *
 * <p>Example of non-compliant code (more than 2 properties on same lines):
 * <pre>
 * &#64;Schema(description = "Auth0 user identifier", example = "auth0|123456789",
 *         requiredMode = Schema.RequiredMode.REQUIRED)
 * </pre>
 *
 * @since 1.0
 */
@StatelessCheck
public class SwaggerAnnotationLineBreakCheck extends AbstractCheck {

    private static final String MSG_PROPERTIES_ON_SEPARATE_LINES =
        "Swagger annotation @%s with more than %d properties must have each property on a separate line.";

    private static final String MSG_OPENING_PAREN_SAME_LINE =
        "Swagger annotation @%s opening parenthesis should be on the same line as the annotation name.";

    /**
     * Set of Swagger/OpenAPI annotations to check.
     */
    private static final Set<String> SWAGGER_ANNOTATIONS = new HashSet<>(Arrays.asList(
        "Schema",
        "Operation",
        "ApiResponse",
        "ApiResponses",
        "Parameter",
        "RequestBody",
        "Header",
        "Content",
        "ExampleObject",
        "ArraySchema"
    ));

    /**
     * Maximum number of properties allowed on the same line.
     * Default is 2.
     */
    private int maxPropertiesSameLine = 2;

    /**
     * Sets the maximum number of properties allowed on the same line.
     *
     * @param maxPropertiesSameLine the maximum number of properties
     */
    public void setMaxPropertiesSameLine(int maxPropertiesSameLine) {
        this.maxPropertiesSameLine = maxPropertiesSameLine;
    }

    @Override
    public int[] getDefaultTokens() {
        return getRequiredTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return getRequiredTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[]{TokenTypes.ANNOTATION};
    }

    @Override
    public void visitToken(DetailAST ast) {
        String annotationName = getAnnotationName(ast);

        if (annotationName == null || !SWAGGER_ANNOTATIONS.contains(annotationName)) {
            return;
        }

        int propertyCount = countAnnotationProperties(ast);

        if (propertyCount <= maxPropertiesSameLine) {
            return;
        }

        if (!arePropertiesOnSeparateLines(ast)) {
            log(ast, String.format(MSG_PROPERTIES_ON_SEPARATE_LINES, annotationName, maxPropertiesSameLine));
        }
    }

    /**
     * Extracts the annotation name from the annotation AST node.
     *
     * @param annotationAst the ANNOTATION token
     * @return the simple name of the annotation, or null if it cannot be determined
     */
    private String getAnnotationName(DetailAST annotationAst) {
        DetailAST atSign = annotationAst.findFirstToken(TokenTypes.AT);

        if (atSign == null) {
            return null;
        }

        DetailAST nameNode = atSign.getNextSibling();

        if (nameNode == null) {
            return null;
        }

        if (nameNode.getType() == TokenTypes.IDENT) {
            return nameNode.getText();
        }

        if (nameNode.getType() == TokenTypes.DOT) {
            return getSimpleNameFromDot(nameNode);
        }

        return null;
    }

    /**
     * Extracts the simple name from a DOT node (qualified name).
     *
     * @param dotNode the DOT token
     * @return the simple name (rightmost identifier)
     */
    private String getSimpleNameFromDot(DetailAST dotNode) {
        DetailAST current = dotNode;

        while (current != null) {
            DetailAST right = current.getLastChild();

            if (right != null && right.getType() == TokenTypes.IDENT) {
                return right.getText();
            }

            if (right != null && right.getType() == TokenTypes.DOT) {
                current = right;
            } else {
                break;
            }
        }

        return null;
    }

    /**
     * Counts the number of properties (annotation member-value pairs) in the annotation.
     *
     * @param annotationAst the ANNOTATION token
     * @return the number of properties
     */
    private int countAnnotationProperties(DetailAST annotationAst) {
        int count = 0;
        DetailAST child = annotationAst.getFirstChild();

        while (child != null) {
            if (child.getType() == TokenTypes.ANNOTATION_MEMBER_VALUE_PAIR) {
                count++;
            }

            child = child.getNextSibling();
        }

        return count;
    }

    /**
     * Checks if all annotation properties are on separate lines.
     *
     * @param annotationAst the ANNOTATION token
     * @return true if all properties are on separate lines, false otherwise
     */
    private boolean arePropertiesOnSeparateLines(DetailAST annotationAst) {
        DetailAST child = annotationAst.getFirstChild();
        int previousLine = -1;

        while (child != null) {
            if (child.getType() == TokenTypes.ANNOTATION_MEMBER_VALUE_PAIR) {
                int currentLine = child.getLineNo();

                if (previousLine != -1 && currentLine == previousLine) {
                    return false;
                }

                previousLine = currentLine;
            }

            child = child.getNextSibling();
        }

        return true;
    }
}
