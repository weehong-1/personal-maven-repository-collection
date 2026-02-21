package com.weehong.checkstyle.checks;

public class InputSwitchSpacingCheckWithComments {

    public void testWithComments(int value) {
        int result = 0;

        switch (value) {
            case 1:
                result = 1;
                break;
            // Comment between case groups

            case 2:
                result = 2;
                break;
            /* Multi-line
               comment */

            default:
                result = 0;
                break;
        }
    }

    public void testWithEmptyCaseBody(int value) {
        int result = 0;

        switch (value) {
            case 1:

            case 2:
                result = 2;
                break;

            default:
                result = 0;
                break;
        }
    }
}
