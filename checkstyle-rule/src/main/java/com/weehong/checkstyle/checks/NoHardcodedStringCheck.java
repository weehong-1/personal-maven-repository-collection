package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Custom Checkstyle check that prohibits hardcoded string literals in code.
 *
 * <p>String literals should be extracted to constants (static final fields) to
 * improve maintainability and avoid duplication. This check flags string literals
 * that are not in an allowed context.
 *
 * <p>Allowed contexts where string literals are permitted:
 * <ul>
 *   <li>Constant declarations (static final fields)</li>
 *   <li>Annotations</li>
 *   <li>Empty strings ("")</li>
 *   <li>Enum constant definitions</li>
 *   <li>Exception constructors (throw new ...)</li>
 * </ul>
 *
 * <p>Example of violation:
 * <pre>
 * if ("admin".equals(role)) { ... } // violation
 * registry.timer("app.metric", "key", "value"); // violation
 * </pre>
 *
 * <p>Correct usage:
 * <pre>
 * private static final String ROLE_ADMIN = "admin";
 * if (ROLE_ADMIN.equals(role)) { ... } // correct
 *
 * private static final String METRIC_NAME = "app.metric";
 * registry.timer(METRIC_NAME, KEY, VALUE); // correct
 * </pre>
 */
public class NoHardcodedStringCheck extends AbstractCheck {

    private static final String MSG_HARDCODED_STRING =
        "Avoid hardcoded string literals. Extract to a constant (static final) field.";

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
        return new int[]{TokenTypes.STRING_LITERAL};
    }

    @Override
    public void visitToken(DetailAST ast) {
        String value = ast.getText();

        if (isEmptyString(value)) {
            return;
        }

        if (isInConstantDeclaration(ast)) {
            return;
        }

        if (isInAnnotation(ast)) {
            return;
        }

        if (isInEnumConstant(ast)) {
            return;
        }

        if (isInThrowStatement(ast)) {
            return;
        }

        log(ast, MSG_HARDCODED_STRING);
    }

    private boolean isEmptyString(String value) {
        return "\"\"".equals(value);
    }

    private boolean isInConstantDeclaration(DetailAST ast) {
        DetailAST current = ast.getParent();

        while (current != null) {
            if (current.getType() == TokenTypes.VARIABLE_DEF) {
                DetailAST modifiers = current.findFirstToken(TokenTypes.MODIFIERS);

                if (modifiers != null) {
                    boolean isStatic = modifiers.findFirstToken(TokenTypes.LITERAL_STATIC) != null;
                    boolean isFinal = modifiers.findFirstToken(TokenTypes.FINAL) != null;

                    return isStatic && isFinal;
                }

                return false;
            }

            current = current.getParent();
        }

        return false;
    }

    private boolean isInAnnotation(DetailAST ast) {
        DetailAST current = ast.getParent();

        while (current != null) {
            if (current.getType() == TokenTypes.ANNOTATION) {
                return true;
            }

            current = current.getParent();
        }

        return false;
    }

    private boolean isInEnumConstant(DetailAST ast) {
        DetailAST current = ast.getParent();

        while (current != null) {
            if (current.getType() == TokenTypes.ENUM_CONSTANT_DEF) {
                return true;
            }

            current = current.getParent();
        }

        return false;
    }

    private boolean isInThrowStatement(DetailAST ast) {
        DetailAST current = ast.getParent();

        while (current != null) {
            if (current.getType() == TokenTypes.LITERAL_THROW) {
                return true;
            }

            current = current.getParent();
        }

        return false;
    }
}
