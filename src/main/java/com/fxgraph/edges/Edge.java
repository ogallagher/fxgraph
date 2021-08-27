package com.fxgraph.edges;

import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;

/**
 * An implementation of {@link AbstractEdge} with a straight connecting line graphic and a label.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 * 
 * @see Edge.EdgeGraphic
 *
 */
public class Edge extends AbstractEdge {
	/**
	 * Text string property for displaying as an edge label.
	 */
	private transient final StringProperty textProperty;

	/**
	 * Convenience constructor for {@link #Edge(ICell, ICell, boolean) Edge(source, target, false)}.
	 * 
	 * @param source Source cell.
	 * @param target Target cell.
	 */
	public Edge(ICell source, ICell target) {
		this(source, target, false);
	}

	/**
	 * @param source Source cell.
	 * @param target Target cell.
	 * @param isDirected Whether this edge is directed.
	 */
	public Edge(ICell source, ICell target, boolean isDirected) {
		super(source, target, isDirected);
		textProperty = new SimpleStringProperty();
	}

	@Override
	public EdgeGraphic getGraphic(Graph graph) {
		return new EdgeGraphic(graph, this, textProperty);
	}
	
	/**
	 * @return The string property defining the edge label text.
	 */
	public StringProperty textProperty() {
		return textProperty;
	}
	
	/**
	 * Fully defined, optionally directed, labeled, straight line edge graphic for display
	 * in a graph canvas.
	 * 
	 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
	 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
	 *
	 */
	public static class EdgeGraphic extends AbstractEdgeGraphic {
		/**
		 * The straight line connecting the edge's source cell to the target cell.
		 * 
		 * @implNote This member is only used and bound to source and target anchor points if the
		 * edge is undirected. If the edge is directed, the {@link AbstractEdgeGraphic#arrow} main line
		 * is used instead.
		 */
		private final Line line;
		
		/**
		 * @param graph The graph to whose canvas the edge graphic will be added. 
		 * @param edge The associated edge graph node.
		 * @param textProperty The edge text property to which this graphic's label will be bound.
		 * 
		 * @see Edge#getGraphic(Graph)
		 */
		public EdgeGraphic(Graph graph, Edge edge, StringProperty textProperty) {
			line = new Line();

			// define source and target points by source and target cell anchor points
			final DoubleBinding sourceX = edge.getSource().getXAnchor(graph);
			final DoubleBinding sourceY = edge.getSource().getYAnchor(graph);
			final DoubleBinding targetX = edge.getTarget().getXAnchor(graph);
			final DoubleBinding targetY = edge.getTarget().getYAnchor(graph);
			
			// select between arrow or line
			if (edge.isDirected()) {
				Region target = graph.getGraphic(edge.getTarget());
				setupArrowIntersect(target, sourceX, sourceY, targetX, targetY);
				// add arrow to edge graphic group
				group.getChildren().add(arrow);
			} 
			else {
				line.startXProperty().bind(sourceX);
				line.startYProperty().bind(sourceY);

				line.endXProperty().bind(targetX);
				line.endYProperty().bind(targetY);
				// add line to edge graphic group
				group.getChildren().add(line);
			}
			
			// bind edge graphic label content to edge text member
			text.textProperty().bind(textProperty);
			text.getStyleClass().add("edge-text");
			
			// bind text to calculated center of label based on variable width and height
			final DoubleProperty textWidth = new SimpleDoubleProperty();
			final DoubleProperty textHeight = new SimpleDoubleProperty();
			text.xProperty().bind(sourceX.add(targetX).divide(2).subtract(textWidth.divide(2)));
			text.yProperty().bind(sourceY.add(targetY).divide(2).subtract(textHeight.divide(2)));
			
			// bind variable width and height properties to label dimensions
			final Runnable recalculateWidth = () -> {
				textWidth.set(text.getLayoutBounds().getWidth());
				textHeight.set(text.getLayoutBounds().getHeight());
			};
			// recalculate label dimensions on parent or text change
			text.parentProperty().addListener((obs, oldVal, newVal) -> recalculateWidth.run());
			text.textProperty().addListener((obs, oldVal, newVal) -> recalculateWidth.run());
			
			// add label to edge graphic group
			group.getChildren().add(text);
			
			// add edge graphic group to pane child widgets
			getChildren().add(group);
		}
		
		/**
		 * @return The line connecting the edge's source cell to the target cell.
		 */
		public Line getLine() {
			return line;
		}
	}
}