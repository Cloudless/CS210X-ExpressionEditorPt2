import javafx.application.Application;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ExpressionEditor extends Application {

	public static void main (String[] args) {
		launch(args);
	}

	/**
	 * Mouse event handler for the entire pane that constitutes the ExpressionEditor
	 */
	private static class MouseEventHandler implements EventHandler<MouseEvent> {
		Pane _pane;
		CompoundExpression _rootExpression;
		Expression _focusedExpression;
		Expression _copyExpression;
		double _lastX;
		double _lastY;
		double _clickedX;
		double _clickedY;

		MouseEventHandler (Pane pane, CompoundExpression rootExpression) {
			_pane = pane;
			_rootExpression = rootExpression;
			_focusedExpression = null;
			_copyExpression = null;
		}

		public void handle (MouseEvent event) {

			final double x = event.getSceneX();
			final double y = event.getSceneY();

			if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
				_clickedX = event.getSceneX();
				_clickedY = event.getSceneY();
			}else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				// so long as an expression is currently in focus...
				if (_focusedExpression != null) {
					//if a copy does not exist, build it
					if (_copyExpression == null && _focusedExpression.getNode().contains(_focusedExpression.getNode().sceneToLocal(x, y))) {
						buildCopy();
					}
					if (_copyExpression != null) {
						//drags around copy
						_copyExpression.getNode().setTranslateX(_copyExpression.getNode().getTranslateX() + (x - _lastX));
						_copyExpression.getNode().setTranslateY(_copyExpression.getNode().getTranslateY() + (y - _lastY));
						//swaps focused expression accordingly
						swap(_focusedExpression, _copyExpression.getNode().getLayoutX() + _copyExpression.getNode().getTranslateX());
					}
				}
			} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
				//if there is currently no copy, then change focus
				if (_copyExpression == null) {
					if (_focusedExpression == null) {
						_focusedExpression = _rootExpression.focus(_clickedX, _clickedY);
					} else {
						((Region) _focusedExpression.getNode()).setBorder(Expression.NO_BORDER);
						_focusedExpression = _focusedExpression.focus(_clickedX, _clickedY);
					}
				} else {
					//if there is a copy, set it down (aka set it to null and remove from pane)
					_focusedExpression.setColor(Color.BLACK);
					_pane.getChildren().remove(_copyExpression.getNode());
					_copyExpression = null;
					System.out.println(_rootExpression.convertToString(0));
				}
			}

			_lastX = x;
			_lastY = y;
		}

		/**
		 * Copies the focused expression and puts the copy in the same location as the original
		 */
		private void buildCopy() {
			_copyExpression = _focusedExpression.deepCopy();
			_focusedExpression.setColor(Expression.GHOST_COLOR);
			_pane.getChildren().add(_copyExpression.getNode());

			Bounds originalBounds = _focusedExpression.getNode().localToScene(_focusedExpression.getNode().getBoundsInLocal());
			Bounds copyBounds = _copyExpression.getNode().localToScene(_copyExpression.getNode().getBoundsInLocal());

			_copyExpression.getNode().setLayoutX(originalBounds.getMinX() - copyBounds.getMinX());
			_copyExpression.getNode().setLayoutY(originalBounds.getMinY()- copyBounds.getMinY());
		}
	}

	public static void swap(Expression e, double x) {
		if (e.getParent() != null && e.getNode() != null) {
			final Pane p = (Pane) e.getNode().getParent();
			// makes a copy of the node's parent's children
			List<Node> currentCase = FXCollections.observableArrayList(p.getChildren());

			final int currentIndex = currentCase.indexOf(e.getNode());
			// +- 2 so as to skip over operation labels
			final int leftIndex = currentIndex - 2;
			final int rightIndex = currentIndex + 2;

			// finding index in expression's parent's children
			final int expressionIndex = (int) currentIndex / 2;
			final int leftExpressionIndex = expressionIndex - 1;
			final int rightExpressionIndex = expressionIndex + 1;

			// determining coordinates in scene
			Bounds currentBoundsInScene = e.getNode().localToScene(e.getNode().getBoundsInLocal());
			final double currentX = currentBoundsInScene.getMinX();
			// if there is no sibling to the left, then the farthest leftwards x coordinate would be that of this expression's JavaFX node
			double leftX = currentX;
			double leftWidth = 0;
			double operatorWidth = 0;

			// determining width of labels representing operations
			if (currentCase.size() > 0) {
				if (currentIndex == 0) {
					operatorWidth = ((Region)currentCase.get(1)).getWidth();
				} else {
					operatorWidth = ((Region)currentCase.get(currentCase.size() - 2)).getWidth();
				}
			}
			// checking if this expression and its JavaFX node should be swapped with its sibling to the left
			// first make sure there is a sibling to the left
			if (leftIndex >= 0) {
				Bounds leftBoundsInScene = p.getChildren().get(leftIndex).localToScene(p.getChildren().get(leftIndex).getBoundsInLocal());
				// if the node of this expression was to be in the left position,
				// then its x coordinate would be that of the leftwards sibling
				leftX = leftBoundsInScene.getMinX();
				leftWidth = leftBoundsInScene.getWidth();

				if (Math.abs(x - leftX) < Math.abs(x - currentX)) {
					Collections.swap(currentCase, currentIndex, leftIndex);
					p.getChildren().setAll(currentCase);
					//also swaps the expression itself, not just its JavaFX node
					swapSubexpressions(e, expressionIndex, leftExpressionIndex);
					return;
				}
			}
			// checking if this expression and its JavaFX node should be swapped with its sibling to the right
			// first make sure there is a sibling to the right
			if (rightIndex < currentCase.size()) {
				Bounds rightBoundsInScene = p.getChildren().get(rightIndex).localToScene(p.getChildren().get(rightIndex).getBoundsInLocal());
				// if the node of this expression was to be in the right position,
				// then its x coordinate would be the coordinate of the left sibling, the width of the left sibling, the width of this expression's node, and the width of any labels in place for operator symbols
				final double rightX = leftX + leftWidth + operatorWidth + rightBoundsInScene.getWidth() + operatorWidth;

				if (Math.abs(x - rightX) < Math.abs(x - currentX)) {
					Collections.swap(currentCase, currentIndex, rightIndex);
					p.getChildren().setAll(currentCase);
					// also swaps the expression itself, not just its JavaFX node
					swapSubexpressions(e, expressionIndex, rightExpressionIndex);
					return;
				}
			}

		}
	}

	/**
	 * Switches placement in parent expression with the sibling at the given index of swapIndex
	 * @param currentIndex index of this expression in its parent's list of children
	 * @param swapIndex index of the sibling to switch with in the parent's list of children
	 */
	private static void swapSubexpressions(Expression e, int currentIndex, int swapIndex) {
		Collections.swap(e.getParent().getChildren(), currentIndex, swapIndex);
	}

	/**
	 * Size of the GUI
	 */
	private static final int WINDOW_WIDTH = 500, WINDOW_HEIGHT = 250;

	/**
	 * Initial expression shown in the textbox
	 */
	private static final String EXAMPLE_EXPRESSION = "2*x+3*y+4*z+(7+6*z)";

	/**
	 * Parser used for parsing expressions.
	 */
	private final ExpressionParser expressionParser = new SimpleExpressionParser();

	@Override
	public void start (Stage primaryStage) {
		primaryStage.setTitle("Expression Editor");

		// Add the textbox and Parser button
		final Pane queryPane = new HBox();
		final TextField textField = new TextField(EXAMPLE_EXPRESSION);
		final Button button = new Button("Parse");
		queryPane.getChildren().add(textField);

		final Pane expressionPane = new Pane();

		// Add the callback to handle when the Parse button is pressed
		button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle (MouseEvent e) {
				// Try to parse the expression
				try {
					// Success! Add the expression's Node to the expressionPane
					final Expression expression = expressionParser.parse(textField.getText(), true);
					System.out.println(expression.convertToString(0));
					expressionPane.getChildren().clear();
					expressionPane.getChildren().add(expression.getNode());
					expression.getNode().setLayoutX(WINDOW_WIDTH/4);
					expression.getNode().setLayoutY(WINDOW_HEIGHT/2);

					// If the parsed expression is a CompoundExpression, then register some callbacks
					if (expression instanceof CompoundExpression) {
						((Pane) expression.getNode()).setBorder(Expression.NO_BORDER);
						final MouseEventHandler eventHandler = new MouseEventHandler(expressionPane, (CompoundExpression) expression);
						expressionPane.setOnMousePressed(eventHandler);
						expressionPane.setOnMouseDragged(eventHandler);
						expressionPane.setOnMouseReleased(eventHandler);
					}
				} catch (ExpressionParseException epe) {
					// If we can't parse the expression, then mark it in red
					textField.setStyle("-fx-text-fill: red");
				}
			}
		});
		queryPane.getChildren().add(button);

		// Reset the color to black whenever the user presses a key
		textField.setOnKeyPressed(e -> textField.setStyle("-fx-text-fill: black"));

		final BorderPane root = new BorderPane();
		root.setTop(queryPane);
		root.setCenter(expressionPane);

		primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
		primaryStage.show();
	}
}