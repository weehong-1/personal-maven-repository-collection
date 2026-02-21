package com.weehong.checkstyle.checks;

import com.weehong.checkstyle.AbstractStatementSpacingCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Custom Checkstyle check that enforces blank line spacing around standalone while statements.
 * A standalone while statement is one that is not part of a do-while loop.
 *
 * <p>This check ensures that standalone while statements are both preceded and followed
 * by a blank line for improved readability, unless they are the first or last statement
 * in a block.
 */
public class WhileSpacingCheck extends AbstractStatementSpacingCheck {

    private static final String MSG_TEMPLATE_BEFORE =
        "Standalone 'while' statement should be preceded by a blank line.";

    private static final String MSG_TEMPLATE_AFTER =
        "Standalone 'while' statement should be followed by a blank line.";

    @Override
    protected String getMessageBefore() {
        return MSG_TEMPLATE_BEFORE;
    }

    @Override
    protected String getMessageAfter() {
        return MSG_TEMPLATE_AFTER;
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[]{TokenTypes.LITERAL_WHILE};
    }

    @Override
    protected boolean shouldCheckSpacing(DetailAST ast) {
        // Only check spacing for standalone while statements (not part of do-while loop)
        DetailAST parent = ast.getParent();

        if (parent == null) {
            return true;
        }

        return parent.getType() != TokenTypes.LITERAL_DO;
    }
}
