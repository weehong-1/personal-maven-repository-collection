package com.weehong.checkstyle.checks;

import com.weehong.checkstyle.AbstractStatementSpacingCheck;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Custom Checkstyle check that enforces blank line spacing around for statements.
 *
 * <p>This check ensures that for statements are both preceded and followed
 * by a blank line for improved readability, unless they are the first or last statement
 * in a block.
 */
public class ForSpacingCheck extends AbstractStatementSpacingCheck {

    private static final String MSG_TEMPLATE_BEFORE =
        "Standalone 'for' statement should be preceded by a blank line.";

    private static final String MSG_TEMPLATE_AFTER =
        "Standalone 'for' statement should be followed by a blank line.";

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
        return new int[]{TokenTypes.LITERAL_FOR};
    }

    // For loops are always standalone, so we use the default shouldCheckSpacing() which returns true
}
