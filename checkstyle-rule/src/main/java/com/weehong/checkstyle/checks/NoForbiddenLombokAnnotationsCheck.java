package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.StatelessCheck;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Checkstyle Check that forbids all Lombok annotations except @Getter and @Setter.
 *
 * <p>This check inspects all annotation usages in class, method, field, and parameter
 * declarations and reports violations for any Lombok annotation that is not in the
 * allowed list.</p>
 *
 * <p>Allowed Lombok annotations:
 * <ul>
 *   <li>@Getter</li>
 *   <li>@Setter</li>
 * </ul>
 * </p>
 *
 * <p>Forbidden Lombok annotations include (but are not limited to):
 * <ul>
 *   <li>@Data</li>
 *   <li>@Value</li>
 *   <li>@Builder</li>
 *   <li>@AllArgsConstructor</li>
 *   <li>@NoArgsConstructor</li>
 *   <li>@RequiredArgsConstructor</li>
 *   <li>@ToString</li>
 *   <li>@EqualsAndHashCode</li>
 *   <li>@Slf4j, @Log, @Log4j, @Log4j2, @CommonsLog, @XSlf4j</li>
 *   <li>@Cleanup</li>
 *   <li>@SneakyThrows</li>
 *   <li>@Synchronized</li>
 *   <li>@With</li>
 *   <li>@Wither</li>
 *   <li>@Delegate</li>
 *   <li>@NonNull</li>
 *   <li>@var, @val</li>
 *   <li>@UtilityClass</li>
 *   <li>@FieldDefaults</li>
 *   <li>@Accessors</li>
 *   <li>@Tolerate</li>
 *   <li>@Jacksonized</li>
 *   <li>@SuperBuilder</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
@StatelessCheck
public class NoForbiddenLombokAnnotationsCheck extends AbstractCheck {

    private static final String MSG_KEY = "Lombok annotation @%s is not allowed. "
        + "Only @Getter and @Setter are permitted.";

    /**
     * Set of allowed Lombok annotations (simple names).
     */
    private static final Set<String> ALLOWED_LOMBOK_ANNOTATIONS = new HashSet<>(Arrays.asList(
        "Getter",
        "Setter"
    ));

    /**
     * Set of forbidden Lombok annotations (simple names).
     * This is a comprehensive list of known Lombok annotations.
     */
    private static final Set<String> FORBIDDEN_LOMBOK_ANNOTATIONS = new HashSet<>(Arrays.asList(
        // Common annotations
        "Data",
        "Value",
        "Builder",
        "AllArgsConstructor",
        "NoArgsConstructor",
        "RequiredArgsConstructor",
        "ToString",
        "EqualsAndHashCode",

        // Logging annotations
        "Slf4j",
        "Log",
        "Log4j",
        "Log4j2",
        "CommonsLog",
        "XSlf4j",
        "Flogger",
        "CustomLog",
        "JBossLog",

        // Utility annotations
        "Cleanup",
        "SneakyThrows",
        "Synchronized",
        "With",
        "Wither",
        "Delegate",
        "NonNull",

        // Variable declaration
        "var",
        "val",

        // Class-level annotations
        "UtilityClass",
        "FieldDefaults",
        "Accessors",
        "ExtensionMethod",

        // Other annotations
        "Tolerate",
        "Jacksonized",
        "SuperBuilder",
        "Singular",
        "Locked",
        "Unlocked"
    ));

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.ANNOTATION
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    @Override
    public void visitToken(DetailAST ast) {
        if (ast.getType() == TokenTypes.ANNOTATION) {
            String annotationName = getAnnotationName(ast);

            if (annotationName != null && isForbiddenLombokAnnotation(annotationName)) {
                log(ast, String.format(MSG_KEY, annotationName));
            }
        }
    }

    /**
     * Extracts the annotation name from the annotation AST node.
     *
     * @param annotationAst the ANNOTATION token
     * @return the simple name of the annotation, or null if it cannot be determined
     */
    private String getAnnotationName(DetailAST annotationAst) {
        // The structure is: ANNOTATION -> AT -> IDENT or DOT
        DetailAST atSign = annotationAst.findFirstToken(TokenTypes.AT);
        if (atSign == null) {
            return null;
        }

        DetailAST nameNode = atSign.getNextSibling();
        if (nameNode == null) {
            return null;
        }

        // Handle simple annotation names (e.g., @Data)
        if (nameNode.getType() == TokenTypes.IDENT) {
            return nameNode.getText();
        }

        // Handle qualified annotation names (e.g., @lombok.Data)
        if (nameNode.getType() == TokenTypes.DOT) {
            return getSimpleNameFromDot(nameNode);
        }

        return null;
    }

    /**
     * Extracts the simple name from a DOT node (qualified name).
     * For example, "lombok.Data" returns "Data".
     *
     * @param dotNode the DOT token
     * @return the simple name (rightmost identifier)
     */
    private String getSimpleNameFromDot(DetailAST dotNode) {
        // DOT nodes have left and right children
        // We want the rightmost IDENT
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
     * Checks if the annotation is a forbidden Lombok annotation.
     *
     * @param annotationName the simple name of the annotation
     * @return true if the annotation is a forbidden Lombok annotation, false otherwise
     */
    private boolean isForbiddenLombokAnnotation(String annotationName) {
        // Check if it's in the forbidden list
        if (FORBIDDEN_LOMBOK_ANNOTATIONS.contains(annotationName)) {
            return true;
        }

        // Check if it's NOT in the allowed list but matches lombok package patterns
        // This catches any Lombok annotation we might have missed
        return !ALLOWED_LOMBOK_ANNOTATIONS.contains(annotationName)
            && isLikelyLombokAnnotation(annotationName);
    }

    /**
     * Heuristic to detect if an annotation is likely a Lombok annotation
     * based on common naming patterns.
     *
     * @param annotationName the simple name of the annotation
     * @return true if the annotation appears to be a Lombok annotation
     */
    private boolean isLikelyLombokAnnotation(String annotationName) {
        // Common Lombok annotation patterns
        // Most Lombok annotations are in PascalCase and follow certain patterns

        // If it's in our allowed list, it's not forbidden
        if (ALLOWED_LOMBOK_ANNOTATIONS.contains(annotationName)) {
            return false;
        }

        // Check against known forbidden list
        if (FORBIDDEN_LOMBOK_ANNOTATIONS.contains(annotationName)) {
            return true;
        }

        // Any other annotation starting with these prefixes is likely Lombok
        // but we'll be conservative and only flag known Lombok annotations
        return false;
    }
}
