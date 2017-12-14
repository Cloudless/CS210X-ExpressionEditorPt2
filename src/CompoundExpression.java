import java.util.List;

interface CompoundExpression extends Expression {
	/**
	 * Adds the specified expression as a child.
	 * 
	 * @param subexpression
	 *            the child expression to add
	 */
	void addSubexpression(Expression subexpression);

	/**
	 * Returns the children of the expression.
	 */
	List<Expression> getChildren();

	/**
	 * Gets the focus at click (x, y) or null if none exists
	 *
	 * @param x
	 *            current x coordinate
	 * @param y
	 *            current y coordinate
	 * @return the new focused Expression
	 */
	Expression focus(double x, double y);

}
