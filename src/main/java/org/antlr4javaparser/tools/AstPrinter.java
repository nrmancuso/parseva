package org.antlr4javaparser.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr4javaparser.tools.grammar.JavaBaseListener;
import org.antlr4javaparser.tools.grammar.JavaLexer;
import org.antlr4javaparser.tools.grammar.JavaParser;

/**
 * A small class that flattens an ANTLR4 {@code ParseTree}. Given the
 * {@code ParseTree}:
 *
 * <pre>
 * {@code
 * a
 * '-- b
 * |   |
 * |   '-- d
 * |       |
 * |       '-- e
 * |           |
 * |           '-- f
 * |
 * '-- c
 * }
 * </pre>
 *
 * This class will flatten this structure as follows:
 *
 * <pre>
 * {@code
 * a
 * '-- b
 * |   |
 * |   '-- f
 * |
 * '-- c
 * }
 * </pre>
 *
 * In other word: all inner nodes that have a single child are removed from the AstPrinter.
 */
public class AstPrinter {

    private static final Pattern NEWLINE = Pattern.compile("\n", Pattern.LITERAL);
    /**
     * The payload will either be the name of the parser rule, or the token
     * of a leaf in the tree.
     */
    private final Object payload;

    /**
     * All child nodes of this AstPrinter.
     */
    private final List<AstPrinter> children;

    public AstPrinter(ParseTree tree) {
        this(null, tree);
    }

    private AstPrinter(AstPrinter ast, ParseTree tree) {
        this(ast, tree, new ArrayList<>());
    }

    private AstPrinter(AstPrinter parent, ParseTree tree, List<AstPrinter> children) {

        payload = getPayload(tree);
        this.children = children;

        if (parent == null) {
            // We're at the root of the AstPrinter, traverse down the parse tree to fill
            // this AstPrinter with nodes.
            walk(tree, this);
        }
        else {
            parent.children.add(this);
        }
    }

    public Object getPayload() {
        return payload;
    }

    public List<AstPrinter> getChildren() {
        return new ArrayList<>(children);
    }

    // Determines the payload of this AstPrinter: a string in case it's an inner node (which
    // is the name of the parser rule), or a Token in case it is a leaf node.
    private Object getPayload(ParseTree tree) {
        if (tree.getChildCount() == 0) {
            // A leaf node: return the tree's payload, which is a Token.
            return tree.getPayload();
        }
        else {
            // The name for parser rule `foo` will be `FooContext`. Strip `Context` and
            // lower case the first character.
            String ruleName = tree.getClass().getSimpleName().replace("Context", "");
            return Character.toLowerCase(ruleName.charAt(0)) + ruleName.substring(1);
        }
    }

    // Fills this AstPrinter based on the parse tree.
    private static void walk(ParseTree tree, AstPrinter ast) {

        if (tree.getChildCount() == 0) {
            // We've reached a leaf. We must create a new instance of an AstPrinter because
            // the constructor will make sure this new instance is added to its parent's
            // child nodes.
            new AstPrinter(ast, tree);
        }
        else if (tree.getChildCount() == 1) {
            // We've reached an inner node with a single child: we don't include this in
            // our AstPrinter.
            walk(tree.getChild(0), ast);
        }
        else if (tree.getChildCount() > 1) {

            for (int i = 0; i < tree.getChildCount(); i++) {

                AstPrinter temp = new AstPrinter(ast, tree.getChild(i));

                if (!(temp.payload instanceof Token)) {
                    // Only traverse down if the payload is not a Token.
                    walk(tree.getChild(i), temp);
                }
            }
        }
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        AstPrinter ast = this;
        List<AstPrinter> firstStack = new ArrayList<>();
        firstStack.add(ast);

        List<List<AstPrinter>> childListStack = new ArrayList<>();
        childListStack.add(firstStack);

        while (!childListStack.isEmpty()) {

            List<AstPrinter> childStack = childListStack.get(childListStack.size() - 1);

            if (childStack.isEmpty()) {
                childListStack.remove(childListStack.size() - 1);
            }
            else {
                ast = childStack.remove(0);
                String caption;

                if (ast.payload instanceof Token) {
                    Token token = (Token) ast.payload;
                    caption = String.format("TOKEN[type: %s, text: %s]",
                        token.getType(), NEWLINE.matcher(token.getText()).replaceAll(Matcher.quoteReplacement("\\n")));
                }
                else {
                    caption = String.valueOf(ast.payload);
                }

                StringBuilder indent = new StringBuilder();

                for (int i = 0; i < childListStack.size() - 1; i++) {
                    if (childListStack.get(i).isEmpty()) {
                        indent.append("   ");
                    } else {
                        indent.append("|  ");
                    }
                }

                if (childStack.isEmpty()) {
                    builder.append(indent)
                        .append("'- ")
                        .append(caption)
                        .append("\n");
                } else {
                    builder.append(indent)
                        .append("|- ")
                        .append(caption)
                        .append("\n");
                }

                if (ast.children.isEmpty()) {
                    continue;
                }
                List<AstPrinter> children = new ArrayList<>(ast.children);
                childListStack.add(children);
            }
        }

        return builder.toString();
    }

    public static String createAstString(String filename) {
        try {
            // Open the input file stream
            CharStream codePointCharStream = CharStreams.fromFileName(filename);

            // Create a lexer that feeds off of input CharStream
            JavaLexer lexer = new JavaLexer(codePointCharStream);

            // Create a buffer of tokens pulled from the lexer
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Create a parser that feeds off the tokens buffer
            JavaParser parser = new JavaParser(tokens);

            // Begin parsing at rule prog
            ParseTree tree = parser.compilationUnit();

            // Create a generic parse tree walker that can trigger callbacks
            ParseTreeWalker walker = new ParseTreeWalker();
            // Walk the tree created during the parse, trigger callbacks
            // Need to implement this listener
            walker.walk(new JavaBaseListener(), tree);
            System.out.println(); // print a \n after translation

            // Walk the tree again to translate to java
            // Need to implement this translator
            //walker.walk(new MyLangTranslator(), tree);

            AstPrinter ast = new AstPrinter(tree);

            //System.out.println(ast);
            return ast.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String... args) {

        try {
            // Open the input file stream
            String fileName
                = "src/test/resources/org/antlr4javaparser/ast/InputBasicRecord.java";
            CharStream codePointCharStream = CharStreams.fromFileName(fileName);

            // Create a lexer that feeds off of input CharStream
            JavaLexer lexer = new JavaLexer(codePointCharStream);

            // Create a buffer of tokens pulled from the lexer
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Create a parser that feeds off the tokens buffer
            JavaParser parser = new JavaParser(tokens);

            // Begin parsing at rule prog
            ParseTree tree = parser.compilationUnit();

            // Create a generic parse tree walker that can trigger callbacks
            ParseTreeWalker walker = new ParseTreeWalker();
            // Walk the tree created during the parse, trigger callbacks
            // Need to implement this listener
            walker.walk(new JavaBaseListener(), tree);
            System.out.println(); // print a \n after translation

            // Walk the tree again to translate to java
            // Need to implement this translator
            //walker.walk(new MyLangTranslator(), tree);

            AstPrinter ast = new AstPrinter(tree);

            System.out.println(ast);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}