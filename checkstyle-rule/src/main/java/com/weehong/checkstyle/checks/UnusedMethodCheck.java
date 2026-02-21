package com.weehong.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Custom Checkstyle check that detects unused private methods.
 *
 * <p>This check identifies private methods that are declared but never invoked
 * within the class. It excludes common framework methods and special cases like
 * main methods, serialization methods, and methods annotated with framework annotations.
 */
public class UnusedMethodCheck extends AbstractCheck {

    private static final String MSG_UNUSED_PRIVATE_METHOD = "Private method ''{0}'' is declared but never used.";

    private final Map<String, Set<String>> declaredPrivateMethods = new HashMap<>();
    private final Map<String, DetailAST> signatureToMethodDef = new HashMap<>();
    private final Set<String> invokedSignatures = new HashSet<>();

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
        return new int[] {TokenTypes.METHOD_DEF, TokenTypes.METHOD_CALL, TokenTypes.METHOD_REF};
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        declaredPrivateMethods.clear();
        signatureToMethodDef.clear();
        invokedSignatures.clear();
    }

    @Override
    public void visitToken(DetailAST ast) {
        if (ast.getType() == TokenTypes.METHOD_DEF) {
            processMethodDefinition(ast);
        } else if (ast.getType() == TokenTypes.METHOD_CALL) {
            processMethodCall(ast);
        } else if (ast.getType() == TokenTypes.METHOD_REF) {
            processMethodReference(ast);
        }
    }

    @Override
    public void finishTree(DetailAST rootAST) {
        checkUnusedMethods();
    }

    private void processMethodDefinition(DetailAST methodDef) {
        if (!isPrivateMethod(methodDef)) {
            return;
        }

        if (isSpecialMethod(methodDef)) {
            return;
        }

        if (hasFrameworkAnnotation(methodDef)) {
            return;
        }

        DetailAST methodNameAST = methodDef.findFirstToken(TokenTypes.IDENT);

        if (methodNameAST != null) {
            String methodName = methodNameAST.getText();
            String signature = buildMethodSignature(methodDef, methodName);

            declaredPrivateMethods
                    .computeIfAbsent(methodName, k -> new HashSet<>())
                    .add(signature);
            signatureToMethodDef.put(signature, methodDef);
        }
    }

    private void processMethodCall(DetailAST methodCall) {
        DetailAST methodNameAST = methodCall.getFirstChild();

        if (methodNameAST != null) {
            String methodName = getMethodName(methodNameAST);

            if (methodName != null) {
                markMethodAsUsed(methodName);
            }
        }
    }

    private void processMethodReference(DetailAST methodRef) {
        DetailAST ident = methodRef.findFirstToken(TokenTypes.IDENT);

        if (ident != null) {
            String methodName = ident.getText();
            markMethodAsUsed(methodName);
        } else {
            DetailAST dot = methodRef.findFirstToken(TokenTypes.DOT);
            if (dot != null) {
                DetailAST lastChild = dot.getLastChild();
                if (lastChild != null && lastChild.getType() == TokenTypes.IDENT) {
                    String methodName = lastChild.getText();
                    markMethodAsUsed(methodName);
                }
            }
        }
    }

    private String getMethodName(DetailAST ast) {
        if (ast.getType() == TokenTypes.IDENT) {
            return ast.getText();
        } else if (ast.getType() == TokenTypes.DOT) {
            DetailAST lastChild = ast.getLastChild();

            if (lastChild != null && lastChild.getType() == TokenTypes.IDENT) {
                return lastChild.getText();
            }
        }

        return null;
    }

    private void markMethodAsUsed(String methodName) {
        // Mark all signatures with this method name as used
        // Without full type resolution, we cannot determine which specific overload
        // is being called, so we mark all overloads as used
        Set<String> signatures = declaredPrivateMethods.get(methodName);
        if (signatures != null) {
            invokedSignatures.addAll(signatures);
        }
        // Also add the plain method name for methods that might be external
        invokedSignatures.add(methodName);
    }

    private String buildMethodSignature(DetailAST methodDef, String methodName) {
        StringBuilder signature = new StringBuilder(methodName);
        signature.append('(');

        DetailAST parameters = methodDef.findFirstToken(TokenTypes.PARAMETERS);
        if (parameters != null) {
            DetailAST parameter = parameters.findFirstToken(TokenTypes.PARAMETER_DEF);
            boolean first = true;

            while (parameter != null) {
                if (!first) {
                    signature.append(',');
                }
                first = false;

                DetailAST type = parameter.findFirstToken(TokenTypes.TYPE);
                if (type != null) {
                    signature.append(getTypeString(type));
                }

                // Check if this is a varargs parameter
                DetailAST ellipsis = parameter.findFirstToken(TokenTypes.ELLIPSIS);
                if (ellipsis != null) {
                    signature.append("...");
                }

                // Advance to next PARAMETER_DEF
                parameter = parameter.getNextSibling();
                while (parameter != null && parameter.getType() != TokenTypes.PARAMETER_DEF) {
                    parameter = parameter.getNextSibling();
                }
            }
        }

        signature.append(')');
        return signature.toString();
    }

    private String getTypeString(DetailAST typeAST) {
        StringBuilder typeStr = new StringBuilder();
        DetailAST child = typeAST.getFirstChild();

        while (child != null) {
            if (child.getType() == TokenTypes.IDENT) {
                typeStr.append(child.getText());
            } else if (child.getType() == TokenTypes.DOT) {
                // Build full qualified name from DOT structure
                FullIdent fullIdent = FullIdent.createFullIdent(child);
                if (fullIdent != null) {
                    typeStr.append(fullIdent.getText());
                }
            } else if (child.getType() == TokenTypes.TYPE_ARGUMENTS) {
                // Process generic type arguments
                typeStr.append('<');
                typeStr.append(getTypeArguments(child));
                typeStr.append('>');
            } else if (child.getType() == TokenTypes.ARRAY_DECLARATOR) {
                typeStr.append("[]");
            } else if (child.getType() == TokenTypes.LITERAL_BOOLEAN) {
                typeStr.append("boolean");
            } else if (child.getType() == TokenTypes.LITERAL_BYTE) {
                typeStr.append("byte");
            } else if (child.getType() == TokenTypes.LITERAL_CHAR) {
                typeStr.append("char");
            } else if (child.getType() == TokenTypes.LITERAL_SHORT) {
                typeStr.append("short");
            } else if (child.getType() == TokenTypes.LITERAL_INT) {
                typeStr.append("int");
            } else if (child.getType() == TokenTypes.LITERAL_LONG) {
                typeStr.append("long");
            } else if (child.getType() == TokenTypes.LITERAL_FLOAT) {
                typeStr.append("float");
            } else if (child.getType() == TokenTypes.LITERAL_DOUBLE) {
                typeStr.append("double");
            } else if (child.getType() == TokenTypes.LITERAL_VOID) {
                typeStr.append("void");
            }
            child = child.getNextSibling();
        }

        return typeStr.toString();
    }

    private String getTypeArguments(DetailAST typeArgumentsAST) {
        StringBuilder args = new StringBuilder();
        DetailAST child = typeArgumentsAST.getFirstChild();
        boolean first = true;

        while (child != null) {
            if (child.getType() == TokenTypes.TYPE_ARGUMENT) {
                if (!first) {
                    args.append(',');
                }
                first = false;
                args.append(getTypeArgument(child));
            }
            child = child.getNextSibling();
        }

        return args.toString();
    }

    private String getTypeArgument(DetailAST typeArgumentAST) {
        StringBuilder arg = new StringBuilder();
        DetailAST child = typeArgumentAST.getFirstChild();

        while (child != null) {
            if (child.getType() == TokenTypes.IDENT) {
                arg.append(child.getText());
            } else if (child.getType() == TokenTypes.DOT) {
                // Handle qualified type in generic argument
                FullIdent fullIdent = FullIdent.createFullIdent(child);
                if (fullIdent != null) {
                    arg.append(fullIdent.getText());
                }
            } else if (child.getType() == TokenTypes.WILDCARD_TYPE) {
                arg.append('?');
                // Check for bounds (extends/super)
                DetailAST extendsClause = child.findFirstToken(TokenTypes.TYPE_UPPER_BOUNDS);
                DetailAST superClause = child.findFirstToken(TokenTypes.TYPE_LOWER_BOUNDS);
                if (extendsClause != null) {
                    arg.append(" extends ");
                    DetailAST boundType = extendsClause.findFirstToken(TokenTypes.TYPE);
                    if (boundType != null) {
                        arg.append(getTypeString(boundType));
                    }
                } else if (superClause != null) {
                    arg.append(" super ");
                    DetailAST boundType = superClause.findFirstToken(TokenTypes.TYPE);
                    if (boundType != null) {
                        arg.append(getTypeString(boundType));
                    }
                }
            } else if (child.getType() == TokenTypes.TYPE_ARGUMENTS) {
                // Nested generics like List<Map<String, Integer>>
                arg.append('<');
                arg.append(getTypeArguments(child));
                arg.append('>');
            } else if (child.getType() == TokenTypes.ARRAY_DECLARATOR) {
                arg.append("[]");
            }
            child = child.getNextSibling();
        }

        return arg.toString();
    }

    private void checkUnusedMethods() {
        for (Map.Entry<String, Set<String>> entry : declaredPrivateMethods.entrySet()) {
            Set<String> signatures = entry.getValue();

            for (String signature : signatures) {
                if (!invokedSignatures.contains(signature)) {
                    DetailAST methodAST = signatureToMethodDef.get(signature);
                    if (methodAST != null) {
                        log(methodAST, MSG_UNUSED_PRIVATE_METHOD, signature);
                    }
                }
            }
        }
    }

    private boolean isPrivateMethod(DetailAST methodDef) {
        DetailAST modifiers = methodDef.findFirstToken(TokenTypes.MODIFIERS);

        if (modifiers == null) {
            return false;
        }

        return modifiers.findFirstToken(TokenTypes.LITERAL_PRIVATE) != null;
    }

    private boolean isSpecialMethod(DetailAST methodDef) {
        DetailAST methodNameAST = methodDef.findFirstToken(TokenTypes.IDENT);

        if (methodNameAST == null) {
            return false;
        }

        String methodName = methodNameAST.getText();

        return "main".equals(methodName)
                || "writeObject".equals(methodName)
                || "readObject".equals(methodName)
                || "readObjectNoData".equals(methodName)
                || "writeReplace".equals(methodName)
                || "readResolve".equals(methodName)
                || methodName.startsWith("setUp")
                || methodName.startsWith("tearDown");
    }

    private boolean hasFrameworkAnnotation(DetailAST methodDef) {
        DetailAST modifiers = methodDef.findFirstToken(TokenTypes.MODIFIERS);

        if (modifiers == null) {
            return false;
        }

        DetailAST annotation = modifiers.findFirstToken(TokenTypes.ANNOTATION);

        while (annotation != null) {
            DetailAST annotationIdent = annotation.findFirstToken(TokenTypes.IDENT);

            if (annotationIdent != null) {
                String annotationName = annotationIdent.getText();

                if (isFrameworkAnnotation(annotationName)) {
                    return true;
                }
            }

            annotation = annotation.getNextSibling();

            if (annotation != null && annotation.getType() != TokenTypes.ANNOTATION) {
                break;
            }
        }

        return false;
    }

    private boolean isFrameworkAnnotation(String annotationName) {
        return "Test".equals(annotationName)
                || "Before".equals(annotationName)
                || "After".equals(annotationName)
                || "BeforeEach".equals(annotationName)
                || "AfterEach".equals(annotationName)
                || "BeforeAll".equals(annotationName)
                || "AfterAll".equals(annotationName)
                || "PostConstruct".equals(annotationName)
                || "PreDestroy".equals(annotationName)
                || "Bean".equals(annotationName)
                || "Scheduled".equals(annotationName)
                || "EventListener".equals(annotationName)
                || annotationName.endsWith("Mapping")
                || annotationName.startsWith("Post")
                || annotationName.startsWith("Pre")
                || annotationName.startsWith("Around");
    }
}
