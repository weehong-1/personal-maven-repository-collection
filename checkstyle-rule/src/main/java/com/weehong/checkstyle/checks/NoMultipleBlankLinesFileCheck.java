package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;
import com.puppycrawl.tools.checkstyle.api.FileText;

import java.io.File;

/**
 * Custom Checkstyle check that prohibits more than one consecutive blank line.
 * This check works at the file level and can be applied to any file type (Java, XML, properties, etc.).
 *
 * <p>This check ensures that there are no multiple consecutive blank lines
 * in any source file, allowing a maximum of one blank line between content.
 */
public class NoMultipleBlankLinesFileCheck extends AbstractFileSetCheck {

    private static final String MSG_MULTIPLE_BLANK_LINES = "More than 1 consecutive blank line is not allowed.";

    @Override
    protected void processFiltered(File file, FileText fileText) {
        final String[] lines = fileText.toLinesArray();
        int consecutiveBlankLines = 0;

        for (int i = 0; i < lines.length; i++) {
            final String line = lines[i];

            if (line.trim().isEmpty()) {
                consecutiveBlankLines++;

                if (consecutiveBlankLines > 1) {
                    log(i + 1, MSG_MULTIPLE_BLANK_LINES);
                }
            } else {
                consecutiveBlankLines = 0;
            }
        }
    }
}
