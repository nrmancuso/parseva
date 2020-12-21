package parseva.tools;

import java.util.ArrayList;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import parseva.tools.api.ASTNode;

public final class ASTNodeParser {

    private ASTNode ast;

    public ASTNode convertParseTree(ParseTree parseTree) {
        final ASTNode rootNode = createRootNode();
    }

    public ASTNodeImpl createRootNode() {
        final ASTNodeImpl rootNode = createASTNode();
    }

    public ASTNodeImpl createASTNode(ParseTree parseTree, ASTNode parent, int index) {
        final ASTNodeImpl node = new ASTNodeImpl();
        if (parseTree.getChildCount() == 0) {
            node.setText(parseTree.getText());
        } else {
            node.setText(getFormattedNodeClassNameWithoutContext(parseTree));
        }
        node.setColumnNumber(getColumn(parseTree));
        // May need to adjust line number below?
        node.setLineNumber(getLine(parseTree));
        node.setIndex(index);
        node.setType(getTokenType(parseTree));
        node.setParent(parent);
        node.setChildren(new ArrayList<>());
        return node;
    }

    /**
     * Gets column number from ParseTree node.
     *
     * @param tree ParseTree node
     * @return column number
     */
    private static int getColumn(ParseTree tree) {
        final int column;
        if (tree instanceof TerminalNode) {
            column = ((TerminalNode) tree).getSymbol().getCharPositionInLine();
        } else {
            final ParserRuleContext rule = (ParserRuleContext) tree;
            column = rule.start.getCharPositionInLine();
        }
        return column;
    }

    /**
     * Gets token type of ParseTree node from JavadocTokenTypes class.
     *
     * @param node ParseTree node.
     * @return token type from JavaTokenTypes
     */
    private static int getTokenType(ParseTree node) {
        final int tokenType;

        if (node.getChildCount() == 0) {
            tokenType = ((TerminalNode) node).getSymbol().getType();
        } else {
            final String className = getNodeClassNameWithoutContext(node);
            tokenType = ASTUtils.getTokenId(convertUpperCamelToUpperUnderscore(className));
        }

        return tokenType;
    }

    /**
     * Gets line number from ParseTree node.
     *
     * @param tree ParseTree node
     * @return line number
     */
    private static int getLine(ParseTree tree) {
        final int line;
        if (tree instanceof TerminalNode) {
            line = ((TerminalNode) tree).getSymbol().getLine() - 1;
        } else {
            final ParserRuleContext rule = (ParserRuleContext) tree;
            line = rule.start.getLine() - 1;
        }
        return line;
    }

    /**
     * Gets class name of ParseTree node and removes 'Context' postfix at the
     * end and formats it.
     *
     * @param node {@code ParseTree} node whose class name is to be formatted and returned
     * @return uppercase class name without the word 'Context' and with appropriately
     * inserted underscores
     */
    private static String getFormattedNodeClassNameWithoutContext(ParseTree node) {
        final String classNameWithoutContext = getNodeClassNameWithoutContext(node);
        return convertUpperCamelToUpperUnderscore(classNameWithoutContext);
    }

    /**
     * Gets class name of ParseTree node and removes 'Context' postfix at the
     * end.
     *
     * @param node ParseTree node.
     * @return class name without 'Context'
     */
    private static String getNodeClassNameWithoutContext(ParseTree node) {
        final String className = node.getClass().getSimpleName();
        // remove 'Context' at the end
        final int contextLength = 7;
        return className.substring(0, className.length() - contextLength);
    }

    /**
     * Converts the given {@code text} from camel case to all upper case with
     * underscores separating each word.
     *
     * @param text The string to convert.
     * @return The result of the conversion.
     */
    private static String convertUpperCamelToUpperUnderscore(String text) {
        final StringBuilder result = new StringBuilder(20);
        boolean first = true;
        for (char letter : text.toCharArray()) {
            if (!first && Character.isUpperCase(letter)) {
                result.append('_');
            }
            result.append(Character.toUpperCase(letter));
            first = false;
        }
        return result.toString();
    }
}
