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
 * Custom Checkstyle check that detects unused imports with enhanced reporting.
 *
 * <p>This check identifies import statements that are not used anywhere in the file.
 * It tracks both regular imports and static imports, and provides detailed reporting
 * about which imports are unused.
 */
public class UnusedImportCheck extends AbstractCheck {

    private static final String MSG_UNUSED_IMPORT =
        "Unused import - ''{0}''.";

    private static final String MSG_DUPLICATE_IMPORT =
        "Duplicate import ''{0}''.";

    private final Map<String, DetailAST> imports = new HashMap<>();
    private final Set<String> referenced = new HashSet<>();
    private final Set<String> staticImports = new HashSet<>();
    private final Map<String, Set<String>> simpleNameToQualified = new HashMap<>();

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
            TokenTypes.IMPORT,
            TokenTypes.STATIC_IMPORT,
            TokenTypes.IDENT,
            TokenTypes.DOT
        };
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        imports.clear();
        referenced.clear();
        staticImports.clear();
        simpleNameToQualified.clear();
    }

    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.IMPORT:
                processImport(ast, false);
                break;

            case TokenTypes.STATIC_IMPORT:
                processImport(ast, true);
                break;

            case TokenTypes.IDENT:
                processIdent(ast);
                break;

            case TokenTypes.DOT:
                processDot(ast);
                break;

            default:
                break;
        }
    }

    @Override
    public void finishTree(DetailAST rootAST) {
        checkUnusedImports();
    }

    private void processImport(DetailAST importAST, boolean isStatic) {
        DetailAST dotAST = importAST.findFirstToken(TokenTypes.DOT);

        if (dotAST == null) {
            return;
        }

        FullIdent name = FullIdent.createFullIdent(dotAST);

        if (name == null) {
            return;
        }

        String fullName = name.getText();

        if (fullName.endsWith(".*")) {
            return;
        }

        if (imports.containsKey(fullName)) {
            log(importAST, MSG_DUPLICATE_IMPORT, fullName);
        } else {
            imports.put(fullName, importAST);

            if (isStatic) {
                staticImports.add(fullName);
            }
        }
    }

    private void processIdent(DetailAST ident) {
        if (isInImportStatement(ident)) {
            return;
        }

        String identText = ident.getText();
        referenced.add(identText);

        checkImportMatch(identText);
    }

    private void processDot(DetailAST dot) {
        if (isInImportStatement(dot)) {
            return;
        }

        FullIdent fullIdent = FullIdent.createFullIdent(dot);

        if (fullIdent != null) {
            String fullName = fullIdent.getText();
            referenced.add(fullName);

            int lastDot = fullName.lastIndexOf('.');
            if (lastDot > 0) {
                String simpleName = fullName.substring(lastDot + 1);
                simpleNameToQualified.computeIfAbsent(simpleName, k -> new HashSet<>()).add(fullName);
            }
        }
    }

    private void checkImportMatch(String identText) {
        for (String importName : imports.keySet()) {
            if (importName.endsWith("." + identText)) {
                referenced.add(importName);
            }

            if (staticImports.contains(importName)) {
                int lastDot = importName.lastIndexOf('.');

                if (lastDot > 0) {
                    String staticMember = importName.substring(lastDot + 1);

                    if (staticMember.equals(identText)) {
                        referenced.add(importName);
                    }
                }
            }
        }
    }

    private void checkUnusedImports() {
        for (Map.Entry<String, DetailAST> entry : imports.entrySet()) {
            String importName = entry.getKey();
            DetailAST importAST = entry.getValue();

            if (!isImportUsed(importName)) {
                log(importAST, MSG_UNUSED_IMPORT, importName);
            }
        }
    }

    private boolean isImportUsed(String importName) {
        if (referenced.contains(importName)) {
            return true;
        }

        int lastDot = importName.lastIndexOf('.');

        if (lastDot > 0) {
            String className = importName.substring(lastDot + 1);

            if (referenced.contains(className)) {
                return true;
            }

            Set<String> qualifiedNames = simpleNameToQualified.get(className);
            if (qualifiedNames != null && qualifiedNames.contains(importName)) {
                return true;
            }
        }

        return false;
    }

    private boolean isInImportStatement(DetailAST ast) {
        DetailAST parent = ast.getParent();

        while (parent != null) {
            int type = parent.getType();

            if (type == TokenTypes.IMPORT || type == TokenTypes.STATIC_IMPORT) {
                return true;
            }

            parent = parent.getParent();
        }

        return false;
    }
}
