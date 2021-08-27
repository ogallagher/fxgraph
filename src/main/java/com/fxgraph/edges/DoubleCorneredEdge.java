package com.fxgraph.edges;

import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * An {@code AbstractEdge} implementation that displays an otherwise diagonal edge as a chain
 * of three vertical and horizontal component segments:
 * <br><br> 
 * HVH or VHV (H=horizontal, V=vertical) 
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 *
 */
public class DoubleCorneredEdge extends AbstractEdge {
	/**
	 * Edge text label.
	 */
	private final StringProperty textProperty;
	/**
	 * Edge orientation, horizontal (HVH) or vertical (VHV).
	 */
	private final Orientation orientation;

	/**
	 * Convenience constructor for 
	 * {@link #DoubleCorneredEdge(ICell, ICell, boolean, Orientation) DoubleCorneredEdge(source, target, false, orientation}.
	 * 
	 * @param source Source cell.
	 * @param target Target cell.
	 * @param orientation Edge orientation.
	 */
	public DoubleCorneredEdge(ICell source, ICell target, Orientation orientation) {
		this(source, target, false, orientation);
	}

	/**
	 * @param source Source cell.
	 * @param target Target cell.
	 * @param isDirected Whether the edge is directed, with a terminal arrow.
	 * @param orientation Edge orientation, horizontal or vertical.
	 */
	public DoubleCorneredEdge(ICell source, ICell target, boolean isDirected, Orientation orientation) {
		super(source, target, isDirected);
		this.orientation = orientation;
		textProperty = new SimpleStringProperty();
	}
	
	@Override
	public EdgeGraphic getGraphic(Graph graph) {
		return new EdgeGraphic(graph, this, orientation, textProperty);
	}
	
	/**
	 * @return Edge text {@link #textProperty}.
	 */
	public StringProperty textProperty() {
		return textProperty;
	}

	/**
	 * {@code AbstractEdgeGraphic} implementation for displaying a double cornered edge in a graph.
	 * 
	 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
	 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
	 *
	 */
	public static class EdgeGraphic extends AbstractEdgeGraphic {
		private final DoubleBinding sourceX;
		private final DoubleBinding sourceY;
		private final DoubleBinding targetX;
		private final DoubleBinding targetY;
		private final DoubleBinding centerX;
		private final DoubleBinding centerY;
		private final Line lineA = new Line();
		private final Line lineB = new Line();
		private final Line lineC = new Line();
		
		/**
		 * @param graph The graph to whose canvas this edge graphic will be added.
		 * @param edge The corresponding double cornered edge.
		 * @param orientation Edge orientation, horizontal or vertical.
		 * @param textProperty Edge text.
		 */
		public EdgeGraphic(Graph graph, DoubleCorneredEdge edge, Orientation orientation, StringProperty textProperty) {
			sourceX = edge.getSource().getXAnchor(graph);
			sourceY = edge.getSource().getYAnchor(graph);
			targetX = edge.getTarget().getXAnchor(graph);
			targetY = edge.getTarget().getYAnchor(graph);

			centerX = sourceX.add(targetX).divide(2);
			centerY = sourceY.add(targetY).divide(2);

			text.textProperty().bind(textProperty);
			text.getStyleClass().add("edge-text");
			final DoubleProperty textWidth = new SimpleDoubleProperty();
			final DoubleProperty textHeight = new SimpleDoubleProperty();
			final Runnable recalculateWidth = () -> {
				textWidth.set(text.getLayoutBounds().getWidth());
				textHeight.set(text.getLayoutBounds().getHeight());
			};
			text.parentProperty().addListener((obs, oldVal, newVal) -> recalculateWidth.run());
			text.textProperty().addListener((obs, oldVal, newVal) -> recalculateWidth.run());

			if(orientation == Orientation.HORIZONTAL) {
				lineA.startXProperty().bind(sourceX);
				lineA.startYProperty().bind(sourceY);
				lineA.endXProperty().bind(centerX);
				lineA.endYProperty().bind(sourceY);
				group.getChildren().add(lineA);

				lineB.startXProperty().bind(centerX);
				lineB.startYProperty().bind(sourceY);
				lineB.endXProperty().bind(centerX);
				lineB.endYProperty().bind(targetY);
				group.getChildren().add(lineB);

				if (edge.isDirected()) {
					Region target = graph.getGraphic(edge.getTarget());
					setupArrowIntersect(target, centerX, targetY, targetX, targetY);
					group.getChildren().add(arrow);
				} 
				else {
					lineC.startXProperty().bind(centerX);
					lineC.startYProperty().bind(targetY);
					lineC.endXProperty().bind(targetX);
					lineC.endYProperty().bind(targetY);
					group.getChildren().add(lineC);
				}
			} 
			else {
				lineA.startXProperty().bind(sourceX);
				lineA.startYProperty().bind(sourceY);
				lineA.endXProperty().bind(sourceX);
				lineA.endYProperty().bind(centerY);
				group.getChildren().add(lineA);

				lineB.startXProperty().bind(sourceX);
				lineB.startYProperty().bind(centerY);
				lineB.endXProperty().bind(targetX);
				lineB.endYProperty().bind(centerY);
				group.getChildren().add(lineB);

				if (edge.isDirected()) {
					Region target = graph.getGraphic(edge.getTarget());
					setupArrowIntersect(target, targetX, centerY, targetX, targetY);
					group.getChildren().add(arrow);
				} 
				else {
					lineC.startXProperty().bind(targetX);
					lineC.startYProperty().bind(centerY);
					lineC.endXProperty().bind(targetX);
					lineC.endYProperty().bind(targetY);
					group.getChildren().add(lineC);
				}
			}
			text.xProperty().bind(centerX.subtract(textWidth.divide(2)));
			text.yProperty().bind(centerY.subtract(textHeight.divide(2)));

			group.getChildren().add(text);
			getChildren().add(group);
		}

		public DoubleBinding getSourceX() {
			return sourceX;
		}

		public DoubleBinding getSourceY() {
			return sourceY;
		}

		public DoubleBinding getTargetX() {
			return targetX;
		}

		public DoubleBinding getTargetY() {
			return targetY;
		}

		public DoubleBinding getCenterX() {
			return centerX;
		}

		public DoubleBinding getCenterY() {
			return centerY;
		}

		/**
		 * @deprecated Redundant.
		 * @see AbstractEdgeGraphic#getGroup()
		 */
		public Group getGroup() {
			return group;
		}

		public Line getLineA() {
			return lineA;
		}

		public Line getLineB() {
			return lineB;
		}

		public Line getLineC() {
			return lineC;
		}
		
		/**
		 * @deprecated Redundant.
		 * @see AbstractEdgeGraphic#getText()
		 */
		public Text getText() {
			return text;
		}

	}

}