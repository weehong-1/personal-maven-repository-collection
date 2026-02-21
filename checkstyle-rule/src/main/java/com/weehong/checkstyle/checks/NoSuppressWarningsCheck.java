package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Custom Checkstyle check that prohibits the use of @SuppressWarnings annotation.
 *
 * <p>This check helps maintain code quality by preventing developers from
 * suppressing compiler warnings. Instead, the root cause of the warning
 * should be addressed.
 */
public class NoSuppressWarningsCheck extends AbstractCheck {

    private static final String MSG_KEY = "Avoid using @SuppressWarnings annotation. "
        + "Fix the underlying issue instead of suppressing the warning.";

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
        DetailAST identifierAst = ast.findFirstToken(TokenTypes.IDENT);

        if (identifierAst != null && "SuppressWarnings".equals(identifierAst.getText())) {
            log(ast, MSG_KEY);
        }
    }
}
