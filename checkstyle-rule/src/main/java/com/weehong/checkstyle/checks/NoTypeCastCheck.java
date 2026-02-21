package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Custom Checkstyle check that prohibits the use of type casting.
 *
 * <p>Type casting can lead to ClassCastException at runtime and often indicates
 * a design issue. This check encourages the use of proper generics, polymorphism,
 * or pattern matching instead of explicit type casts.
 */
public class NoTypeCastCheck extends AbstractCheck {

    private static final String MSG_KEY = "Avoid using type casting. "
        + "Consider using generics, polymorphism, or instanceof pattern matching instead.";

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
        return new int[]{TokenTypes.TYPECAST};
    }

    @Override
    public void visitToken(DetailAST ast) {
        log(ast, MSG_KEY);
    }
}
