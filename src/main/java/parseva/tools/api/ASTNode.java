package parseva.tools.api;

/**
 * This is an interface for the ASTNode construct.  Since antlr4 doesn't support
 * ast nodes, we are defining one here.
 */
public interface ASTNode {

    /**
     * Node type.
     *
     * @return node type.
     */
    int getType();

    /**
     * Node text.
     *
     * @return node text
     */
    String getText();

    /**
     * Node line number.
     *
     * @return node line number
     */
    int getLineNumber();

    /**
     * Node column number.
     *
     * @return node column number.
     */
    int getColumnNumber();

    /**
     * Array of children.
     *
     * @return array of children
     */
    ASTNode[] getChildren();

    /**
     * Parent node.
     *
     * @return parent node.
     */
    ASTNode getParent();

    /**
     * Node index among parent's children.
     *
     * @return index
     */
    int getIndex();
}