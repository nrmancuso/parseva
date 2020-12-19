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

    public static final int CAPACITY = 1024;
    private static final Pattern NEWLINE = Pattern.compile("\n", Pattern.LITERAL);

    public static String buildAstString(JavaAST ast) {

        List<JavaAST> firstStack = new ArrayList<>();
        firstStack.add(ast);

        List<List<JavaAST>> childListStack = new ArrayList<>();
        childListStack.add(firstStack);

        StringBuilder builder = new StringBuilder(CAPACITY);
        while (!childListStack.isEmpty()) {

            List<JavaAST> childStack = childListStack.get(childListStack.size() - 1);

            if (childStack.isEmpty()) {
                childListStack.remove(childListStack.size() - 1);
            }
            else {
                JavaAST javaAst = childStack.remove(0);
                String caption;

                if (javaAst.getPayload() instanceof Token token) {
                    caption = String.format("TOKEN[type: %s, text: %s]",
                        token.getType(), NEWLINE.matcher(token.getText()).replaceAll(Matcher.quoteReplacement("\\n")));
                }
                else {
                    caption = String.valueOf(javaAst.getPayload());
                }

                StringBuilder indent = new StringBuilder(CAPACITY);

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

                if (javaAst.getChildren().isEmpty()) {
                    continue;
                }
                List<JavaAST> astChildren = new ArrayList<>(javaAst.getChildren());
                childListStack.add(astChildren);
            }
        }

        return builder.toString();
    }

    public static String createAstString(String filename) {
        String astString = null;
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

            JavaAST ast = new JavaAST(tree);

            //System.out.println(ast);
            astString = buildAstString(ast);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return astString;
    }
}