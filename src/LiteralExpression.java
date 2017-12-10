import javafx.scene.Node;
import javafx.scene.control.Label;
/**
 *
 * An expression that has a literal value -> [0-9]+ | [a-z]
 *
 */
public class LiteralExpression implements Expression {

    // instance variables
    private CompoundExpression _parent;
    private String _value;
    private Node _node;

    /**
     * Returns the JavaFX node associated with this expression.
     * @return the JavaFX node associated with this expression.
     */
    public Node getNode (){
        if(_node != null){
            return _node;
        }
        _node = new Label(_value);
        return _node;
    }

    public String getValue() {
        return _value;
    }

    /**
     * Constructs a literal expression
     *
     * @param value
     *            the value of the expression [0,9]+ | [a,z]
     */
    public LiteralExpression(String value) {
        _value = value;
    }

    /**
     * Returns the expression's parent.
     *
     * @return the expression's parent
     */
    public CompoundExpression getParent() {
        return _parent;
    }

    /**
     * Sets the parent be the specified expression.
     *
     * @param parent
     *            the CompoundExpression that should be the parent of the target
     *            object
     */
    public void setParent(CompoundExpression parent) {
        _parent = parent;
    }

    /**
     * Creates and returns a deep copy of the expression. The entire tree rooted at
     * the target node is copied, i.e., the copied Expression is as deep as
     * possible.
     *
     * @return the deep copy
     */
    public Expression deepCopy() {
        return new LiteralExpression(_value);
    }

    /**
     * Recursively flattens the expression as much as possible throughout the entire
     * tree. Specifically, in every multiplicative or additive expression x whose
     * first or last child c is of the same type as x, the children of c will be
     * added to x, and c itself will be removed. This method modifies the expression
     * itself.
     */
    public void flatten() {
        // No flatten method necessary because Literal expressions have no children.
    }

    /**
     * Creates a String representation by recursively printing out (using
     * indentation) the tree represented by this expression, starting at the
     * specified indentation level.
     *
     * @param indentLevel
     *            the indentation level (number of tabs from the left margin) at
     *            which to start
     * @return a String representation of the expression tree.
     */
    public String convertToString(int indentLevel) {
        String converted = _value;
        for (int i = 0; i < indentLevel; i++) {
            converted = "\t" + converted; // add specified number of tabs
        }
        converted = converted + "\n"; // add a new line at the end
        return (converted);
    }

    public void setFocus(){
        _node.setStyle("-fx-border-style: solid");
        _node.setStyle("-fx-border-color: red");
    }
}
