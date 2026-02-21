package com.weehong.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Abstract base class for statement spacing checks.
 *
 * <p>This check ensures that statements are both preceded and followed
 * by a blank line for improved readability, unless they are the first or last statement
 * in a block.
 */
public abstract class AbstractStatementSpacingCheck extends AbstractCheck {

    /**
     * Gets the message for when a blank line is required before the statement.
     *
     * @return the message template
     */
    protected abstract String getMessageBefore();

    /**
     * Gets the message for when a blank line is required after the statement.
     *
     * @return the message template
     */
    protected abstract String getMessageAfter();

    /**
     * Determines if this statement should be checked for spacing.
     * Subclasses can override this to implement specific logic (e.g., skip else-if chains).
     *
     * @param ast the statement AST node
     * @return true if spacing should be checked
     */
    protected boolean shouldCheckSpacing(DetailAST ast) {
        return true;
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
    public void visitToken(DetailAST ast) {
        if (!shouldCheckSpacing(ast)) {
            return;
        }

        if (!isFirstStatementInBlock(ast)) {
            int lineNo = ast.getLineNo();
            DetailAST previousSibling = getPreviousSibling(ast);

            if (previousSibling != null) {
                int previousEndLine = getLastLineOfNode(previousSibling);
                int blankLinesBetween = lineNo - previousEndLine - 1;

                if (blankLinesBetween < 1) {
                    log(ast, getMessageBefore());
                }
            }
        }

        if (!isLastStatementInBlock(ast)) {
            int endLine = getLastLineOfNode(ast);
            DetailAST nextSibling = getNextSibling(ast);

            if (nextSibling != null) {
                int nextStartLine = nextSibling.getLineNo();
                int blankLinesBetween = nextStartLine - endLine - 1;

                if (blankLinesBetween < 1) {
                    log(ast, getMessageAfter());
                }
            }
        }
    }

    /**
     * Checks if this is the first statement in a block.
     *
     * @param ast the AST node to check
     * @return true if this is the first statement in a block
     */
    protected boolean isFirstStatementInBlock(DetailAST ast) {
        DetailAST parent = ast.getParent();

        if (parent == null) {
            return true;
        }

        if (parent.getType() == TokenTypes.SLIST) {
            DetailAST firstChild = parent.getFirstChild();

            while (firstChild != null && firstChild.getType() == TokenTypes.LCURLY) {
                firstChild = firstChild.getNextSibling();
            }

            return firstChild == ast;
        }

        return false;
    }

    /**
     * Checks if this is the last statement in a block.
     *
     * @param ast the AST node to check
     * @return true if this is the last statement in a block
     */
    protected boolean isLastStatementInBlock(DetailAST ast) {
        DetailAST parent = ast.getParent();

        if (parent == null) {
            return true;
        }

        if (parent.getType() == TokenTypes.SLIST) {
            DetailAST lastChild = parent.getLastChild();
            // Skip closing brace if present

            while (lastChild != null && lastChild.getType() == TokenTypes.RCURLY) {
                lastChild = lastChild.getPreviousSibling();
            }

            return lastChild == ast;
        }

        return false;
    }

    /**
     * Gets the previous sibling statement.
     *
     * @param ast the current AST node
     * @return the previous sibling, or null if none exists
     */
    protected DetailAST getPreviousSibling(DetailAST ast) {
        DetailAST parent = ast.getParent();

        if (parent == null) {
            return null;
        }

        DetailAST previous = null;
        DetailAST current = parent.getFirstChild();

        while (current != null && current != ast) {
            if (isStatement(current)) {
                previous = current;
            }

            current = current.getNextSibling();
        }

        return previous;
    }

    /**
     * Gets the next sibling statement.
     *
     * @param ast the current AST node
     * @return the next sibling, or null if none exists
     */
    protected DetailAST getNextSibling(DetailAST ast) {
        DetailAST parent = ast.getParent();

        if (parent == null) {
            return null;
        }

        DetailAST current = ast.getNextSibling();

        while (current != null) {
            if (isStatement(current)) {
                return current;
            }

            current = current.getNextSibling();
        }

        return null;
    }

    /**
     * Checks if the given AST node represents a statement.
     *
     * @param ast the AST node to check
     * @return true if it's a statement
     */
    protected boolean isStatement(DetailAST ast) {
        int type = ast.getType();
        return type != TokenTypes.LCURLY
            && type != TokenTypes.RCURLY
            && type != TokenTypes.SEMI;
    }

    /**
     * Gets the last line number of a node (including all children).
     *
     * @param ast the AST node
     * @return the last line number
     */
    protected int getLastLineOfNode(DetailAST ast) {
        int lastLine = ast.getLineNo();
        DetailAST child = ast.getFirstChild();

        while (child != null) {
            int childLastLine = getLastLineOfNode(child);

            if (childLastLine > lastLine) {
                lastLine = childLastLine;
            }

            child = child.getNextSibling();
        }

        return lastLine;
    }
}
