package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Custom Checkstyle check that detects unused local variables and parameters.
 *
 * <p>This check tracks variable declarations and their usage within methods,
 * constructors, and blocks to identify variables that are declared but never used.
 */
public class UnusedVariableCheck extends AbstractCheck {

    private static final String MSG_UNUSED_LOCAL_VARIABLE =
        "Local variable ''{0}'' is declared but never used.";

    private static final String MSG_UNUSED_PARAMETER =
        "Parameter ''{0}'' is declared but never used.";

    private final Map<String, DetailAST> declaredVariables = new HashMap<>();
    private final Set<String> usedVariables = new HashSet<>();
    private boolean inMethodOrConstructor = false;

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
        return new int[]{
            TokenTypes.METHOD_DEF,
            TokenTypes.CTOR_DEF,
            TokenTypes.VARIABLE_DEF,
            TokenTypes.PARAMETER_DEF,
            TokenTypes.IDENT
        };
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        declaredVariables.clear();
        usedVariables.clear();
        inMethodOrConstructor = false;
    }

    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.METHOD_DEF:
            case TokenTypes.CTOR_DEF:
                if (inMethodOrConstructor) {
                    return;
                }
                inMethodOrConstructor = true;
                declaredVariables.clear();
                usedVariables.clear();
                break;

            case TokenTypes.VARIABLE_DEF:
                if (inMethodOrConstructor && isLocalVariable(ast)) {
                    DetailAST nameAST = ast.findFirstToken(TokenTypes.IDENT);

                    if (nameAST != null) {
                        String varName = nameAST.getText();
                        declaredVariables.put(varName, ast);
                    }
                }
                break;

            case TokenTypes.PARAMETER_DEF:
                if (inMethodOrConstructor) {
                    DetailAST nameAST = ast.findFirstToken(TokenTypes.IDENT);

                    if (nameAST != null) {
                        String paramName = nameAST.getText();

                        if (isExceptionParameter(ast)) {
                            break;
                        }

                        DetailAST methodOrCtor = getParentMethodOrConstructor(ast);

                        if (methodOrCtor != null && !isOverrideMethod(methodOrCtor)
                            && !isMainMethod(methodOrCtor)
                            && !isSerializationMethod(methodOrCtor)) {
                            declaredVariables.put(paramName, ast);
                        }
                    }
                }
                break;

            case TokenTypes.IDENT:
                if (inMethodOrConstructor) {
                    String identName = ast.getText();

                    if (declaredVariables.containsKey(identName) && !isDeclaration(ast)) {
                        usedVariables.add(identName);
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void leaveToken(DetailAST ast) {
        if (ast.getType() == TokenTypes.METHOD_DEF || ast.getType() == TokenTypes.CTOR_DEF) {
            if (inMethodOrConstructor) {
                checkUnusedVariables();
                declaredVariables.clear();
                usedVariables.clear();
                inMethodOrConstructor = false;
            }
        }
    }

    private void checkUnusedVariables() {
        for (Map.Entry<String, DetailAST> entry : declaredVariables.entrySet()) {
            String varName = entry.getKey();
            DetailAST varAST = entry.getValue();

            if (!usedVariables.contains(varName)) {
                if (varAST.getType() == TokenTypes.PARAMETER_DEF) {
                    log(varAST, MSG_UNUSED_PARAMETER, varName);
                } else {
                    log(varAST, MSG_UNUSED_LOCAL_VARIABLE, varName);
                }
            }
        }
    }

    private boolean isLocalVariable(DetailAST variableDef) {
        DetailAST parent = variableDef.getParent();
        return parent != null && (parent.getType() == TokenTypes.SLIST
            || parent.getType() == TokenTypes.FOR_INIT
            || parent.getType() == TokenTypes.FOR_EACH_CLAUSE);
    }

    private boolean isDeclaration(DetailAST ident) {
        DetailAST parent = ident.getParent();

        if (parent == null) {
            return false;
        }

        int parentType = parent.getType();
        return parentType == TokenTypes.VARIABLE_DEF
            || parentType == TokenTypes.PARAMETER_DEF;
    }

    private DetailAST getParentMethodOrConstructor(DetailAST ast) {
        DetailAST current = ast;

        while (current != null) {
            int type = current.getType();

            if (type == TokenTypes.METHOD_DEF || type == TokenTypes.CTOR_DEF) {
                return current;
            }

            current = current.getParent();
        }

        return null;
    }

    private boolean isOverrideMethod(DetailAST methodDef) {
        DetailAST modifiers = methodDef.findFirstToken(TokenTypes.MODIFIERS);

        if (modifiers == null) {
            return false;
        }

        DetailAST annotation = modifiers.findFirstToken(TokenTypes.ANNOTATION);

        while (annotation != null) {
            DetailAST annotationIdent = annotation.findFirstToken(TokenTypes.IDENT);

            if (annotationIdent != null && "Override".equals(annotationIdent.getText())) {
                return true;
            }

            annotation = annotation.getNextSibling();

            if (annotation != null && annotation.getType() != TokenTypes.ANNOTATION) {
                break;
            }
        }

        return false;
    }

    private boolean isMainMethod(DetailAST methodDef) {
        DetailAST methodNameAST = methodDef.findFirstToken(TokenTypes.IDENT);

        if (methodNameAST == null || !"main".equals(methodNameAST.getText())) {
            return false;
        }

        DetailAST modifiers = methodDef.findFirstToken(TokenTypes.MODIFIERS);

        if (modifiers == null) {
            return false;
        }

        boolean isPublic = modifiers.findFirstToken(TokenTypes.LITERAL_PUBLIC) != null;
        boolean isStatic = modifiers.findFirstToken(TokenTypes.LITERAL_STATIC) != null;

        return isPublic && isStatic;
    }

    private boolean isSerializationMethod(DetailAST methodDef) {
        DetailAST methodNameAST = methodDef.findFirstToken(TokenTypes.IDENT);

        if (methodNameAST == null) {
            return false;
        }

        String methodName = methodNameAST.getText();
        return "writeObject".equals(methodName)
            || "readObject".equals(methodName)
            || "readObjectNoData".equals(methodName)
            || "writeReplace".equals(methodName)
            || "readResolve".equals(methodName);
    }

    private boolean isExceptionParameter(DetailAST parameterDef) {
        DetailAST parent = parameterDef.getParent();

        while (parent != null) {
            if (parent.getType() == TokenTypes.LITERAL_CATCH) {
                return true;
            }

            parent = parent.getParent();
        }

        return false;
    }
}
