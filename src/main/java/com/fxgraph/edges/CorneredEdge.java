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
 * An {@code AbstractEdge} implementation that segments an otherwise diagonal edge
 * into one horizontal and one vertical segment, having up to one corner. 
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 *
 */
public class CorneredEdge extends AbstractEdge {
	/**
	 * Edge label text.
	 */
	private final StringProperty textProperty;
	/**
	 * A horizontal cornered edge connects the left/right side of a source cell with the
	 * top/bottom side of a target cell. A vertical edge connects the source top/bottom to the
	 * target left/right.
	 */
	private final Orientation orientation;

	/**
	 * Convenience constructor for {@link #CorneredEdge(ICell, ICell, boolean, Orientation)}, being not
	 * directed.
	 * 
	 * @param source Source cell.
	 * @param target Target cell.
	 * @param orientation Edge orientation.
	 */
	public CorneredEdge(ICell source, ICell target, Orientation orientation) {
		this(source, target, false, orientation);
	}

	/**
	 * {@link CorneredEdge} constructor.
	 * 
	 * @param source Source cell.
	 * @param target Target cell.
	 * @param isDirected Whether the edge is directed.
	 * @param orientation Whether the edge is horizontal or vertical.
	 */
	public CorneredEdge(ICell source, ICell target, boolean isDirected, Orientation orientation) {
		super(source, target, isDirected);
		this.orientation = orientation;
		textProperty = new SimpleStringProperty();
	}

	@Override
	public EdgeGraphic getGraphic(Graph graph) {
		return new EdgeGraphic(graph, this, orientation, textProperty);
	}

	/**
	 * @return {@link #textProperty}.
	 */
	public StringProperty textProperty() {
		return textProperty;
	}

	/**
	 * {@code AbstractEdgeGraphic} implementation for a cornered edge.
	 * 
	 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
	 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
	 *
	 */
	public static class EdgeGraphic extends AbstractEdgeGraphic {
		/**
		 * {@link EdgeGraphic CorneredEdge.EdgeGraphic} constructor.
		 * 
		 * @param graph
		 * @param edge
		 * @param orientation
		 * @param textProperty
		 * 
		 * @implSpec The corner is created by creating a line A to span the source-target horizontal gap, and then a line B
		 * to span the source-target vertical gap (if horizontal, vice versa if vertical).
		 */
		public EdgeGraphic(Graph graph, CorneredEdge edge, Orientation orientation, StringProperty textProperty) {
			super();
			
			final DoubleBinding sourceX = edge.getSource().getXAnchor(graph);
			final DoubleBinding sourceY = edge.getSource().getYAnchor(graph);
			final DoubleBinding targetX = edge.getTarget().getXAnchor(graph);
			final DoubleBinding targetY = edge.getTarget().getYAnchor(graph);

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

			if (orientation == Orientation.HORIZONTAL) {
				final Line lineA = new Line();
				lineA.startXProperty().bind(sourceX);
				lineA.startYProperty().bind(sourceY);
				lineA.endXProperty().bind(targetX);
				lineA.endYProperty().bind(sourceY);
				group.getChildren().add(lineA);

				if (edge.isDirected()) {
					Region target = graph.getGraphic(edge.getTarget());
					setupArrowIntersect(target, targetX, sourceY, targetX, targetY);
					group.getChildren().add(arrow);
				} 
				else {
					final Line lineB = new Line();
					lineB.startXProperty().bind(targetX);
					lineB.startYProperty().bind(sourceY);
					lineB.endXProperty().bind(targetX);
					lineB.endYProperty().bind(targetY);
					group.getChildren().add(lineB);
				}

				text.xProperty().bind(targetX.subtract(textWidth.divide(2)));
				text.yProperty().bind(sourceY.subtract(textHeight.divide(2)));
			} 
			else {
				final Line lineA = new Line();
				lineA.startXProperty().bind(sourceX);
				lineA.startYProperty().bind(sourceY);
				lineA.endXProperty().bind(sourceX);
				lineA.endYProperty().bind(targetY);
				group.getChildren().add(lineA);

				if (edge.isDirected()) {
					Region target = graph.getGraphic(edge.getTarget());
					setupArrowIntersect(target, sourceX, targetY, targetX, targetY);
					group.getChildren().add(arrow);
				} 
				else {
					final Line lineB = new Line();
					lineB.startXProperty().bind(sourceX);
					lineB.startYProperty().bind(targetY);
					lineB.endXProperty().bind(targetX);
					lineB.endYProperty().bind(targetY);
					group.getChildren().add(lineB);
				}

				text.xProperty().bind(sourceX.subtract(textWidth.divide(2)));
				text.yProperty().bind(targetY.subtract(textHeight.divide(2)));
			}

			group.getChildren().add(text);
			getChildren().add(group);
		}

		/**
		 * @deprecated Redundant.
		 * 
		 * @see AbstractEdgeGraphic#getGroup()
		 */
		public Group getGroup() {
			return group;
		}
		
		/**
		 * @deprecated Redundant.
		 * 
		 * @see AbstractEdgeGraphic#getText()
		 */
		public Text getText() {
			return text;
		}
	}
}