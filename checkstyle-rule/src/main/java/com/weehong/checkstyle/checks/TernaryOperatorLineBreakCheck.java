package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Custom Checkstyle check that enforces ternary operators to have the '?' and ':'
 * operators at the beginning of new lines.
 *
 * <p>Correct format:
 * <pre>
 * UUID activityUuid = venue.getActivity() != null
 *     ? venue.getActivity().getUuid()
 *     : null;
 * </pre>
 *
 * <p>Incorrect formats:
 * <pre>
 * // '?' on same line as condition
 * UUID activityUuid = venue.getActivity() != null ? venue.getActivity().getUuid()
 *     : null;
 *
 * // All on one line
 * UUID activityUuid = venue.getActivity() != null ? venue.getActivity().getUuid() : null;
 * </pre>
 */
public class TernaryOperatorLineBreakCheck extends AbstractCheck {

    private static final String MSG_QUESTION_MARK_NEW_LINE =
        "Ternary operator '?' must be at the beginning of a new line.";

    private static final String MSG_COLON_NEW_LINE =
        "Ternary operator ':' must be at the beginning of a new line.";

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
        return new int[]{TokenTypes.QUESTION};
    }

    @Override
    public void visitToken(DetailAST ast) {
        DetailAST parent = ast.getParent();

        if (parent == null) {
            return;
        }

        int parentType = parent.getType();

        if (parentType != TokenTypes.EXPR && parentType != TokenTypes.ASSIGN
            && parentType != TokenTypes.LITERAL_RETURN && parentType != TokenTypes.ELIST
            && parentType != TokenTypes.LAMBDA && parentType != TokenTypes.SLIST) {
            return;
        }

        checkTernaryFormatting(ast);
    }

    private void checkTernaryFormatting(DetailAST questionToken) {
        DetailAST firstChild = questionToken.getFirstChild();

        if (firstChild == null) {
            return;
        }

        int conditionEndLine = getLastLineOfExpression(firstChild);
        int questionLine = questionToken.getLineNo();

        if (questionLine == conditionEndLine) {
            log(questionToken, MSG_QUESTION_MARK_NEW_LINE);
        }

        DetailAST colonToken = findColonInChildren(questionToken);

        if (colonToken != null) {
            DetailAST thenExpression = firstChild.getNextSibling();

            if (thenExpression != null && thenExpression.getType() != TokenTypes.COLON) {
                int thenExpressionEndLine = getLastLineOfExpression(thenExpression);
                int colonLine = colonToken.getLineNo();

                if (colonLine == thenExpressionEndLine) {
                    log(colonToken, MSG_COLON_NEW_LINE);
                }
            }
        }
    }

    private DetailAST findColonInChildren(DetailAST questionToken) {
        DetailAST child = questionToken.getFirstChild();

        while (child != null) {
            if (child.getType() == TokenTypes.COLON) {
                return child;
            }

            child = child.getNextSibling();
        }

        return null;
    }

    private int getLastLineOfExpression(DetailAST ast) {
        int lastLine = ast.getLineNo();
        DetailAST child = ast.getFirstChild();

        while (child != null) {
            int childLastLine = getLastLineOfExpression(child);

            if (childLastLine > lastLine) {
                lastLine = childLastLine;
            }

            child = child.getNextSibling();
        }

        return lastLine;
    }

}
