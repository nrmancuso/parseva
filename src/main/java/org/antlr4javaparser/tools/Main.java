package org.antlr4javaparser.tools;

import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr4javaparser.tools.grammar.JavaBaseListener;
import org.antlr4javaparser.tools.grammar.JavaLexer;
import org.antlr4javaparser.tools.grammar.JavaParser;

public class Main {
    public static void main(String... args) {

        try {
            // Open the input file stream
            String fileName = "src/test/resources/org/antlr4javaparser/antlr-test/Test.java";
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
            walker.walk(new JavaBaseListener(), tree);

            // Print LISP-style tree
            System.out.println(tree.toStringTree(parser));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}