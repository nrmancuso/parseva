package org.antlr4javaparser.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.jetbrains.annotations.NotNull;

public final class JavaAST implements ParseTree {

    /**
     * The payload will either be the name of the parser rule, or the token
     * of a leaf in the tree.
     */
    private final Object payload;

    /**
     * All child nodes of this AstPrinter.
     */
    private final List<JavaAST> children;

    private final JavaAST parent;

    public JavaAST(ParseTree tree) {
        this(null, tree);
    }

    JavaAST(JavaAST ast, ParseTree tree) {
        this(ast, tree, new ArrayList<>());
    }

    private JavaAST(JavaAST parent, ParseTree tree, List<JavaAST> children) {

        payload = getPayload(tree);
        this.children = children;
        this.parent = parent;
        setup(tree);
    }

    private void setup(ParseTree tree) {
        if (parent == null) {
            // We're at the root of the AstPrinter, traverse down the parse tree to fill
            // this AstPrinter with nodes.
            AstWalker.walk(tree, this);
        }
        else {
            parent.children.add(this);
        }
    }

    public Object getPayload(@NotNull ParseTree tree) {
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


    @Override
    public JavaAST getParent() {
        return parent;
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    @Override
    public ParseTree getChild(int i) {
        return children.get(i);
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    public void addChild(JavaAST ast) {
        children.add(ast);
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public String toStringTree() {
        return null;
    }

    @Override
    public void setParent(RuleContext parent) {

    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
        return null;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public String toStringTree(Parser parser) {
        return null;
    }

    @Override
    public Interval getSourceInterval() {
        return null;
    }

    public List<JavaAST> getChildren() {
        return Collections.unmodifiableList(children);
    }
}
