package com.weehong.checkstyle.checks;

public class InputReturnStatementSpacingCheck {

    private static final int DEFAULT_MAX_LIFETIME = 30;

    // OK: return is first statement in block
    int firstInBlock(Integer value) {
        if (value == null) {
            return DEFAULT_MAX_LIFETIME;
        }

        return value;
    }

    // Violation: missing blank line before return
    int missingBlankLine(int a, int b) {
        int result = a + b;
        return result;
    }

    // OK: return is the only statement in method
    int onlyStatement() {
        return DEFAULT_MAX_LIFETIME;
    }

    // OK: blank line present before return
    int withBlankLine(int a, int b) {
        int result = a + b;

        return result;
    }

    // Violation: missing blank line before return after if block
    int afterIfBlock(Integer hikari) {
        if (hikari == null) {
            return DEFAULT_MAX_LIFETIME;
        }
        return hikari;
    }

    // OK: blank line present after if block
    int afterIfBlockWithBlankLine(Integer hikari) {
        if (hikari == null) {
            return DEFAULT_MAX_LIFETIME;
        }

        return hikari;
    }

    // Violation: missing blank line before return after multiple statements
    int multipleStatements(int a, int b) {
        int x = a + 1;
        int y = b + 1;
        int result = x + y;
        return result;
    }

    // OK: void return first in block
    void voidReturnFirstInBlock(boolean condition) {
        if (condition) {
            return;
        }

        System.out.println("done");
    }

    // Violation: void return after statement
    void voidReturnAfterStatement(boolean condition) {
        System.out.println("processing");
        return;
    }
}
