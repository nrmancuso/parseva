package parseva.tools;

import java.util.ArrayList;
import java.util.List;

import parseva.tools.api.ASTNode;

public class ASTNodeImpl implements ASTNode {

    /**
     * Node index among parent's children.
     */
    private int index;

    /**
     * Node type.
     */
    private int type;

    public void setIndex(int index) {
        this.index = index;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public void setChildren(List<ASTNode> children) {
        this.children = children;
    }

    public void setParent(ASTNode parent) {
        this.parent = parent;
    }

    /**
     * Node's text content.
     */
    private String text;

    /**
     * Line number.
     */
    private int lineNumber;

    /**
     * Column number.
     */
    private int columnNumber;

    /**
     * Array of child nodes.
     */
    private List<ASTNode> children;

    /**
     * Parent node.
     */
    private ASTNode parent;

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public int getLineNumber() {
        return 0;
    }

    @Override
    public int getColumnNumber() {
        return 0;
    }

    @Override
    public List<ASTNode> getChildren() {
        return new ArrayList<>(this.children);
    }

    @Override
    public ASTNode getParent() {
        return null;
    }

    @Override
    public int getIndex() {
        return 0;
    }
}
