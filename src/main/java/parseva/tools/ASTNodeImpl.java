package parseva.tools;

import parseva.tools.api.ASTNode;

public class ASTNodeImpl implements ASTNode {
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
    public ASTNode[] getChildren() {
        return new ASTNode[0];
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
