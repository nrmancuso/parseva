import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Main {
    public static void main(String... args) {

        try {
            // Open the input file stream
            String fileName = "src/test/antlr-test/Test.java";
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

            // Print LISP-style tree
            System.out.println(tree.toStringTree(parser));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}