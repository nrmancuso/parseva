import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Main {
    public static void main(String... args) {

        try {
            // Open the input file stream
            String fileName = "src/test/antlr-test/Test.java";
            File file = new File(fileName);
            FileInputStream fis = new FileInputStream(file);

            // Create a CharStream that reads from standard input
            ANTLRInputStream input = new ANTLRInputStream(fis);

            // Create a lexer that feeds off of input CharStream
            JavaLexer lexer = new JavaLexer(input);

            // Create a buffer of tokens pulled from the lexer
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Create a parser that feeds off the tokens buffer
            JavaParser parser = new JavaParser(tokens);

            // Begin parsing at rule prog
            ParseTree tree = parser.compilationUnit();

            // Close the input file
            fis.close();

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