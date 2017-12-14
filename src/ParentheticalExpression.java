import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 *
 * An expression that is enclosed by parenthesis.
 *
 */
public class ParentheticalExpression extends AbstractCompoundExpression {

	private Node _node;

	/**
	 * Returns the JavaFX node associated with this expression.
	 * 
	 * @return the JavaFX node associated with this expression.
	 */
	public Node getNode() {
		if (_node != null) {
			return _node;
		}
		HBox hbox = new HBox();
		hbox.getChildren().add(new Label("("));
		hbox.getChildren().add(this.getChildren().get(0).getNode());
		hbox.getChildren().add(new Label(")"));
		_node = hbox;
		return _node;
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
	 * @param indentLevel
	 *            the number of times the operation should be indented
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
	 * 
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