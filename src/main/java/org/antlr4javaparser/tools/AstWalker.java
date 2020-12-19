package org.antlr4javaparser.tools;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

public final class AstWalker {

    private AstWalker() {
    }

    static void walk(ParseTree tree, JavaAST ast) {

        if (tree.getChildCount() == 0) {
            // We've reached a leaf. We must create a new instance of an AstPrinter because
            // the constructor will make sure this new instance is added to its parent's
            // child nodes.
            new JavaAST(ast, tree);
        }
        if (tree.getChildCount() == 1) {
            // We've reached an inner node with a single child: we don't include this in
            // our JavaAST.
            walk(tree.getChild(0), ast);
        }
        else if (tree.getChildCount() > 1) {

            for (int i = 0; i < tree.getChildCount(); i++) {

                JavaAST temp = new JavaAST(ast, tree.getChild(i));

                if (!(temp.getPayload() instanceof Token)) {
                    // Only traverse down if the payload is not a Token.
                    walk(tree.getChild(i), temp);
                }
            }
        }
    }
}
