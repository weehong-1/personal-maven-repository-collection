package com.weehong.checkstyle.checks;

import com.weehong.checkstyle.AbstractStatementSpacingCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Checkstyle check that enforces blank line spacing around and within switch statements.
 *
 * <p>This check ensures that:
 * <ul>
 *   <li>Switch statements are preceded and followed by a blank line (unless first/last in block)</li>
 *   <li>The first case group has a blank line after it</li>
 *   <li>Middle case groups have blank lines before and after them</li>
 *   <li>The last case group (including default) has a blank line before it</li>
 * </ul>
 */
public class SwitchSpacingCheck extends AbstractStatementSpacingCheck {

    private static final String MSG_TEMPLATE_BEFORE =
        "Standalone 'switch' statement should be preceded by a blank line.";

    private static final String MSG_TEMPLATE_AFTER =
        "Standalone 'switch' statement should be followed by a blank line.";

    private static final String MSG_CASE_AFTER =
        "First case group should be followed by a blank line.";

    private static final String MSG_CASE_BEFORE =
        "Case group should be preceded by a blank line.";

    private static final String MSG_CASE_MIDDLE_AFTER =
        "Middle case group should be followed by a blank line.";

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
        return new int[]{TokenTypes.LITERAL_SWITCH};
    }

    @Override
    public void visitToken(DetailAST ast) {
        super.visitToken(ast);

        if (ast.getType() == TokenTypes.LITERAL_SWITCH) {
            checkCaseGroupSpacing(ast);
        }
    }

    private void checkCaseGroupSpacing(DetailAST switchAst) {
        // CASE_GROUP nodes are direct children of LITERAL_SWITCH
        DetailAST[] caseGroups = getCaseGroups(switchAst);

        if (caseGroups.length == 0) {
            return;
        }

        for (int i = 0; i < caseGroups.length; i++) {
            DetailAST caseGroup = caseGroups[i];
            boolean isFirst = i == 0;
            boolean isLast = i == caseGroups.length - 1;

            if (isFirst) {
                checkFirstCaseGroup(caseGroup, caseGroups.length > 1);
            } else if (isLast) {
                checkLastCaseGroup(caseGroup);
            } else {
                checkMiddleCaseGroup(caseGroup);
            }
        }
    }

    private DetailAST[] getCaseGroups(DetailAST slist) {
        List<DetailAST> groups = new ArrayList<>();
        DetailAST child = slist.getFirstChild();

        while (child != null) {
            if (child.getType() == TokenTypes.CASE_GROUP) {
                groups.add(child);
            }

            child = child.getNextSibling();
        }

        return groups.toArray(new DetailAST[0]);
    }

    private void checkFirstCaseGroup(DetailAST caseGroup, boolean hasMoreGroups) {
        if (!hasMoreGroups) {
            return;
        }

        DetailAST lastStatement = getLastStatementInCaseGroup(caseGroup);

        if (lastStatement == null) {
            return;
        }

        DetailAST nextGroup = getNextCaseGroup(caseGroup);

        if (nextGroup != null) {
            int lastLine = getLastLineOfNode(lastStatement);
            int nextLine = nextGroup.getLineNo();
            int blankLines = nextLine - lastLine - 1;

            if (blankLines < 1) {
                log(lastStatement, MSG_CASE_AFTER);
            }
        }
    }

    private void checkLastCaseGroup(DetailAST caseGroup) {
        DetailAST previousGroup = getPreviousCaseGroup(caseGroup);

        if (previousGroup == null) {
            return;
        }

        DetailAST lastStatementPrev = getLastStatementInCaseGroup(previousGroup);

        if (lastStatementPrev == null) {
            return;
        }

        int lastLine = getLastLineOfNode(lastStatementPrev);
        int currentLine = caseGroup.getLineNo();
        int blankLines = currentLine - lastLine - 1;

        if (blankLines < 1) {
            log(caseGroup, MSG_CASE_BEFORE);
        }
    }

    private void checkMiddleCaseGroup(DetailAST caseGroup) {
        DetailAST previousGroup = getPreviousCaseGroup(caseGroup);

        if (previousGroup != null) {
            DetailAST lastStatementPrev = getLastStatementInCaseGroup(previousGroup);

            if (lastStatementPrev != null) {
                int lastLine = getLastLineOfNode(lastStatementPrev);
                int currentLine = caseGroup.getLineNo();
                int blankLines = currentLine - lastLine - 1;

                if (blankLines < 1) {
                    log(caseGroup, MSG_CASE_BEFORE);
                }
            }
        }

        DetailAST lastStatement = getLastStatementInCaseGroup(caseGroup);

        if (lastStatement != null) {
            DetailAST nextGroup = getNextCaseGroup(caseGroup);

            if (nextGroup != null) {
                int lastLine = getLastLineOfNode(lastStatement);
                int nextLine = nextGroup.getLineNo();
                int blankLines = nextLine - lastLine - 1;

                if (blankLines < 1) {
                    log(lastStatement, MSG_CASE_MIDDLE_AFTER);
                }
            }
        }
    }

    private DetailAST getLastStatementInCaseGroup(DetailAST caseGroup) {
        DetailAST slist = caseGroup.findFirstToken(TokenTypes.SLIST);

        DetailAST container = (slist != null)
            ? slist
            : caseGroup;
        DetailAST lastChild = container.getLastChild();

        // Skip non-statement tokens
        while (lastChild != null &&
            (lastChild.getType() == TokenTypes.RCURLY ||
                lastChild.getType() == TokenTypes.LITERAL_CASE ||
                lastChild.getType() == TokenTypes.LITERAL_DEFAULT ||
                lastChild.getType() == TokenTypes.COLON)) {
            lastChild = lastChild.getPreviousSibling();
        }

        return lastChild;
    }

    private DetailAST getNextCaseGroup(DetailAST caseGroup) {
        DetailAST sibling = caseGroup.getNextSibling();

        while (sibling != null) {
            if (sibling.getType() == TokenTypes.CASE_GROUP) {
                return sibling;
            }

            sibling = sibling.getNextSibling();
        }

        return null;
    }

    private DetailAST getPreviousCaseGroup(DetailAST caseGroup) {
        DetailAST sibling = caseGroup.getPreviousSibling();

        while (sibling != null) {
            if (sibling.getType() == TokenTypes.CASE_GROUP) {
                return sibling;
            }

            sibling = sibling.getPreviousSibling();
        }

        return null;
    }
}
