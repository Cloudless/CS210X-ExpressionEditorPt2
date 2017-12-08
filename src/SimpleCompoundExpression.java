import javafx.scene.Node;
import javafx.scene.control.Label;

import java.util.ArrayList;

/**
 *
 * An additive or multiplicative expression
 *
 */
public class SimpleCompoundExpression extends AbstractCompoundExpression {

    /**
     * Returns the JavaFX node associated with this expression.
     * @return the JavaFX node associated with this expression.
     */
    public Node getNode (){
        String labelText = "" + this.getChildren().get(0).getNode();
        for (int i = 1; i < this.getChildren().size(); i++) {
            labelText = labelText + _operation + this.getChildren().get(i).getNode();
        }
        Label label = new Label(labelText);
        return label;
    }

    // instance variable
    private String _operation;

    /**
     * Constructor for SimpleCompoundExpression
     *
     * @param operation
     *            the operation (* or +)
     */
    public SimpleCompoundExpression(String operation) {
        super();
        _operation = operation;
    }

    /**
     * Method that flattens the expression tree.
     */
    public void flatten() {
        final ArrayList<Expression> toAdd = new ArrayList<Expression>();
        for (Expression e : this.getChildren()) {
            e.flatten(); // recursively call flatten on children
            if (e.getClass() == this.getClass()) { // Check if children is a SimpleCompoundExpression
                if (this._operation.equals(((SimpleCompoundExpression) e)._operation)) { // Check if operation of
                    // children is the same.
                    for (Expression c : ((SimpleCompoundExpression) e).getChildren()) {
                        toAdd.add(c); // adds children of children with the same operation to toAdd.
                    }
                } else {
                    toAdd.add(e); // adds the child to toAdd if the operation is different
                }
            } else {
                toAdd.add(e); // adds the child to toAdd if it is of type literal or parenthetical
            }
        }
        this.clearSubexpression(); // clears subexpressions so that the order will stay the same
        for (Expression a : toAdd) {
            this.addSubexpression(a); // adds all Expressions in toAdd as children of this
        }
    }

    /**
     * Method that converts the expression tree into a String
     *
     * @param indentLevel
     *            the number of times the operation should be indented
     * @return the string representation of the expression
     */
    @Override
    public String convertToString(int indentLevel) {
        String converted = _operation;
        for (int i = 0; i < indentLevel; i++) {
            converted = "\t" + converted; // add specified number of tabs
        }
        converted = converted + "\n"; // add a new line at the end
        for (Expression e : this.getChildren()) {
            converted = converted + e.convertToString(indentLevel + 1); // add children strings recursively
        }
        return (converted);
    }

    /**
     * Method that creates a deep copy of this expression
     *
     * @return the copied expression
     */
    public Expression deepCopy() {
        final Expression copy = new SimpleCompoundExpression(_operation);
        for (Expression e : this.getChildren()) {
            ((AbstractCompoundExpression) copy).addSubexpression(e.deepCopy());
        }
        return copy;
    }
}