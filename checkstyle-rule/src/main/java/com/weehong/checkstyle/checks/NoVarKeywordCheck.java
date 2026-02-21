package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Custom Checkstyle check that prohibits the use of the 'var' keyword.
 *
 * <p>This check enforces that concrete data types must be explicitly declared
 * instead of using type inference with the 'var' keyword. This improves code
 * readability and makes the type information explicit.
 *
 * <p>Example of violation:
 * <pre>
 * var list = new ArrayList&lt;String&gt;(); // violation
 * var count = 10; // violation
 * </pre>
 *
 * <p>Correct usage:
 * <pre>
 * List&lt;String&gt; list = new ArrayList&lt;&gt;(); // correct
 * int count = 10; // correct
 * </pre>
 */
public class NoVarKeywordCheck extends AbstractCheck {

    private static final String MSG_VAR_NOT_ALLOWED =
        "Use of ''var'' keyword is not allowed. Concrete data type must be explicitly declared.";

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
        return new int[]{TokenTypes.VARIABLE_DEF};
    }

    @Override
    public void visitToken(DetailAST ast) {
        final DetailAST typeAST = ast.findFirstToken(TokenTypes.TYPE);

        if (typeAST == null) {
            return;
        }

        final DetailAST identAST = typeAST.getFirstChild();

        if (identAST != null && identAST.getType() == TokenTypes.IDENT) {
            final String typeName = identAST.getText();

            if ("var".equals(typeName)) {
                log(identAST, MSG_VAR_NOT_ALLOWED);
            }
        }
    }
}
