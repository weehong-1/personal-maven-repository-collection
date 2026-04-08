package com.weehong.checkstyle.checks;

import com.weehong.checkstyle.AbstractStatementSpacingCheck;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Custom Checkstyle check that enforces a blank line before {@code return} statements.
 *
 * <p>A blank line is required before a {@code return} statement unless it is the first
 * statement in a block. This improves readability by visually separating the return
 * from preceding logic.
 *
 * <p>Example of valid code:
 * <pre>
 * if (condition) {
 *     return DEFAULT_VALUE; // OK - first statement in block
 * }
 *
 * return computedValue; // OK - preceded by blank line
 * </pre>
 *
 * <p>Example of invalid code:
 * <pre>
 * int result = compute();
 * return result; // Violation - missing blank line before return
 * </pre>
 */
public class ReturnStatementSpacingCheck extends AbstractStatementSpacingCheck {

    private static final String MSG_BEFORE =
        "'return' statement should be preceded by a blank line.";

    private static final String MSG_AFTER =
        "'return' statement should be followed by a blank line.";

    @Override
    protected String getMessageBefore() {
        return MSG_BEFORE;
    }

    @Override
    protected String getMessageAfter() {
        return MSG_AFTER;
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[]{TokenTypes.LITERAL_RETURN};
    }
}
