/*
 * Copyright (c) 2014 by Bart Kiers
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
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
 * In other word: all inner nodes that have a single child are removed from the AST.
 */
public class AST {

    private static final Pattern NEWLINE = Pattern.compile("\n", Pattern.LITERAL);
    /**
     * The payload will either be the name of the parser rule, or the token
     * of a leaf in the tree.
     */
    private final Object payload;

    /**
     * All child nodes of this AST.
     */
    private final List<AST> children;

    public AST(ParseTree tree) {
        this(null, tree);
    }

    private AST(AST ast, ParseTree tree) {
        this(ast, tree, new ArrayList<>());
    }

    private AST(AST parent, ParseTree tree, List<AST> children) {

        payload = getPayload(tree);
        this.children = children;

        if (parent == null) {
            // We're at the root of the AST, traverse down the parse tree to fill
            // this AST with nodes.
            walk(tree, this);
        }
        else {
            parent.children.add(this);
        }
    }

    public Object getPayload() {
        return payload;
    }

    public List<AST> getChildren() {
        return new ArrayList<>(children);
    }

    // Determines the payload of this AST: a string in case it's an inner node (which
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

    // Fills this AST based on the parse tree.
    private static void walk(ParseTree tree, AST ast) {

        if (tree.getChildCount() == 0) {
            // We've reached a leaf. We must create a new instance of an AST because
            // the constructor will make sure this new instance is added to its parent's
            // child nodes.
            new AST(ast, tree);
        }
        else if (tree.getChildCount() == 1) {
            // We've reached an inner node with a single child: we don't include this in
            // our AST.
            walk(tree.getChild(0), ast);
        }
        else if (tree.getChildCount() > 1) {

            for (int i = 0; i < tree.getChildCount(); i++) {

                AST temp = new AST(ast, tree.getChild(i));

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

        AST ast = this;
        List<AST> firstStack = new ArrayList<>();
        firstStack.add(ast);

        List<List<AST>> childListStack = new ArrayList<>();
        childListStack.add(firstStack);

        while (!childListStack.isEmpty()) {

            List<AST> childStack = childListStack.get(childListStack.size() - 1);

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
                List<AST> children = new ArrayList<>(ast.children);
                childListStack.add(children);
            }
        }

        return builder.toString();
    }

    public static void main(String... args) {

        try {
            // Open the input file stream
            String fileName
                = "src/test/resources/org/antlr4-javaparser/test/inputs/ast/InputBasicRecord.java";
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

            AST ast = new AST(tree);

            System.out.println(ast);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}