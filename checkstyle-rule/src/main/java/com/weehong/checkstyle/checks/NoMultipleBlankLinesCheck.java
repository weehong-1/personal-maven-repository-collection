package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;

/**
 * Custom Checkstyle check that prohibits more than one consecutive blank line.
 *
 * <p>This check ensures that there are no multiple consecutive blank lines
 * in the source code, allowing a maximum of one blank line between code elements.
 */
public class NoMultipleBlankLinesCheck extends AbstractCheck {

    private static final String MSG_MULTIPLE_BLANK_LINES =
        "More than 1 consecutive blank line is not allowed.";

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
        return new int[0];
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        final FileContents contents = getFileContents();
        final String[] lines = contents.getLines();

        int consecutiveBlankLines = 0;

        for (int i = 0; i < lines.length; i++) {
            final String line = lines[i];

            if (line.trim().isEmpty()) {
                consecutiveBlankLines++;

                if (consecutiveBlankLines > 1) {
                    log(i + 1, 0, MSG_MULTIPLE_BLANK_LINES);
                }
            } else {
                consecutiveBlankLines = 0;
            }
        }
    }
}
