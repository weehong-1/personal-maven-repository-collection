package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Custom Checkstyle check that enforces method parameters to be on separate lines
 * when a method has more than 4 parameters.
 *
 * <p>This check ensures that methods with more than 4 parameters have each parameter
 * on a separate line for improved readability.
 */
public class MethodParameterLineBreakCheck extends AbstractCheck {

    private static final String MSG_PARAMETERS_ON_SEPARATE_LINES =
        "Method with more than 4 parameters must have each parameter on a separate line.";

    private static final int MAX_PARAMETERS_SAME_LINE = 4;

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
        return new int[]{TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF};
    }

    @Override
    public void visitToken(DetailAST ast) {
        DetailAST parameters = ast.findFirstToken(TokenTypes.PARAMETERS);

        if (parameters == null) {
            return;
        }

        int paramCount = countParameters(parameters);

        if (paramCount <= MAX_PARAMETERS_SAME_LINE) {
            return;
        }

        if (!areParametersOnSeparateLines(parameters)) {
            log(ast, MSG_PARAMETERS_ON_SEPARATE_LINES);
        }
    }

    /**
     * Counts the number of parameters in a parameter list.
     *
     * @param parameters the PARAMETERS node
     * @return the number of parameters
     */
    private int countParameters(DetailAST parameters) {
        int count = 0;
        DetailAST param = parameters.findFirstToken(TokenTypes.PARAMETER_DEF);

        while (param != null) {
            count++;
            param = param.getNextSibling();

            while (param != null && param.getType() != TokenTypes.PARAMETER_DEF) {
                param = param.getNextSibling();
            }
        }

        return count;
    }

    /**
     * Checks if all parameters are on separate lines.
     *
     * @param parameters the PARAMETERS node
     * @return true if all parameters are on separate lines, false otherwise
     */
    private boolean areParametersOnSeparateLines(DetailAST parameters) {
        DetailAST param = parameters.findFirstToken(TokenTypes.PARAMETER_DEF);

        if (param == null) {
            return true;
        }

        int previousLine = -1;

        while (param != null) {
            int currentLine = param.getLineNo();

            if (previousLine != -1 && currentLine == previousLine) {
                return false;
            }

            previousLine = currentLine;
            param = param.getNextSibling();

            while (param != null && param.getType() != TokenTypes.PARAMETER_DEF) {
                param = param.getNextSibling();
            }
        }

        return true;
    }
}
