package org.antlr4javaparser.tools;

import java.util.Collections;
import java.util.List;

public final class JavaAST {

    /**
     * All child nodes of this JavaAST.
     */
    private List<JavaAST> children;
    private final JavaAST previousSibling;
    private final int lineNumber;
    private final int columnNumber;
    private final int type;
    private JavaAST parent;

    public JavaAST(int lineNumber,
                   int columnNumber, int type, JavaAST parent,
                   JavaAST previousSibling) {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.type = type;
        this.parent = parent;
        this.previousSibling = previousSibling;
    }

    public JavaAST getParent() {
        return parent;
    }

    public void setParent(JavaAST parent) {
        this.parent = parent;
    }

    public int getChildCount() {
        return children.size();
    }

    public void addChild(JavaAST ast) {
        children.add(ast);
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    public String getText() {
        return null;
    }

    public List<JavaAST> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public String toString() {
        return "JavaAST{" +
            "children=" + children +
            ", previousSibling=" + previousSibling +
            ", lineNumber=" + lineNumber +
            ", columnNumber=" + columnNumber +
            ", type=" + type +
            ", parent=" + parent +
            '}';
    }
}
