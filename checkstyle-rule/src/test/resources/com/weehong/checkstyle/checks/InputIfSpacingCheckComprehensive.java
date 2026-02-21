package com.weehong.checkstyle.checks;

public class InputIfSpacingCheckComprehensive {

    // If as first statement in block - should NOT require preceding blank line
    void firstStatementInBlock() {
        if (true) {
            System.out.println("first");
        }

        int x = 1;
    }

    // If as last statement in block - should NOT require following blank line
    void lastStatementInBlock() {
        int x = 1;

        if (true) {
            System.out.println("last");
        }
    }

    // If in middle of block - requires both preceding and following blank lines
    void middleStatement() {
        int x = 1;
        if (true) {  // violation: needs preceding blank line
            System.out.println("middle");
        }
        int y = 2;  // violation: needs following blank line
    }

    // Correct formatting - with blank lines
    void correctFormatting() {
        int x = 1;

        if (true) {
            System.out.println("correct");
        }

        int y = 2;
    }

    // Else-if chain - the 'if' after 'else' should NOT be checked
    void elseIfChain() {
        int x = 1;

        if (true) {
            System.out.println("first");
        } else if (true) {
            System.out.println("else-if should be ignored");
        } else {
            System.out.println("else");
        }

        int y = 2;
    }

    // Multiple sequential if statements - all need blank lines
    void multipleIfs() {
        int x = 1;

        if (true) {
            System.out.println("first if");
        }

        if (true) {
            System.out.println("second if");
        }

        if (true) {
            System.out.println("third if");
        }

        int y = 2;
    }

    // Multi-line if condition - tests getLastLineOfNode
    void multiLineCondition() {
        int x = 1;

        if (true
            && true
            && true) {
            System.out.println("multi-line condition");
        }

        int y = 2;
    }

    // Nested ifs
    void nestedIfs() {
        int x = 1;

        if (true) {
            if (true) {  // inner if is first in block - no preceding blank needed
                System.out.println("nested");
            }
        }

        int y = 2;
    }
}
