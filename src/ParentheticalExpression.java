import javafx.scene.Node;

/**
 *
 * An expression that is enclosed by parenthesis.
 *
 */
public class ParentheticalExpression extends AbstractCompoundExpression {

    /**
     * Returns the JavaFX node associated with this expression.
     * @return the JavaFX node associated with this expression.
     */
    public Node getNode (){
        return null;
    }

    /**
     * Method that flattens the expression tree.
     */
    public void flatten() {
        for (Expression e : this.getChildren()) {
            e.flatten(); // recursively call flatten on children
        }
    }

    /**
     * Constructor for ParentheticalExpressions
     */
    public ParentheticalExpression() {
        super();
    }

    /**
     * Method that converts the expression tree into a String
     *
     * @param indentLevel the number of times the operation should be indented
     * @return the string representation of the expression
     */
    @Override
    public String convertToString(int indentLevel) {
        String converted = "()";
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
     * @return the copied expression
     */
    public Expression deepCopy() {
        final Expression copy = new ParentheticalExpression();
        for (Expression e : this.getChildren()) {
            ((AbstractCompoundExpression) copy).addSubexpression(e.deepCopy());
        }
        return copy;
    }

}