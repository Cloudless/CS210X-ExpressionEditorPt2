import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;

import java.util.*;
/**
 *
 * Common code in all types of compound expressions
 *
 */
public abstract class AbstractCompoundExpression implements CompoundExpression {

    private CompoundExpression _parent;
    private List<Expression> _children;

    /**
     * Constructor for AbstractCompoundExpressions
     */
    protected AbstractCompoundExpression() {
        _children = new ArrayList<Expression>();
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
     * Returns the expression's list of children.
     *
     * @return the expression's list of children
     */
    public List<Expression> getChildren() {
        return _children;
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
    public abstract Expression deepCopy();

    /**
     * Recursively flattens the expression as much as possible throughout the entire
     * tree. Specifically, in every multiplicative or additive expression x whose
     * first or last child c is of the same type as x, the children of c will be
     * added to x, and c itself will be removed. This method modifies the expression
     * itself.
     */
    public abstract void flatten();

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
    public abstract String convertToString(int indentLevel);

    /**
     * Adds the specified expression as a child.
     *
     * @param subexpression
     *            the child expression to add
     */
    public void addSubexpression(Expression subexpression) {
        _children.add(subexpression);
        subexpression.setParent(this);
    }

    /**
     * Clears all subexpressions from this Expression.
     */
    public void clearSubexpression() {
        _children = new ArrayList<Expression>();
    }

    /**
     * Focuses on the subexpression at scene coordinates (x,y)
     * If no subexpression is clicked, lor no subexpression exists, returns null
     * @param x scene x coordinate
     * @param y scene y coordinate
     * @return the new focused Expression
     */
    public Expression focus(double x, double y) {

        for(Expression child : _children) {

            final Bounds boundsInScene = child.getNode().localToScene(child.getNode().getBoundsInLocal());

            final double xMin = boundsInScene.getMinX();
            final double xMax = boundsInScene.getMaxX();
            final double yMin = boundsInScene.getMinY();
            final double yMax = boundsInScene.getMaxY();

            if (((x <= xMax) && (x >= xMin)) && ((y <= yMax) && (y >= yMin))) {
                ((Pane)child.getNode()).setBorder(RED_BORDER);
                return child;
            }
        }
        return null;
    }
}
