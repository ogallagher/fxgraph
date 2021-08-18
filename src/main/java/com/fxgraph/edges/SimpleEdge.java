/**
 * 
 */
package com.fxgraph.edges;

import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.graph.IEdge;

import javafx.beans.binding.DoubleBinding;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;

/**
 * A straight, diagonal edge connecting two cells in a graph. It can be directed. It does not
 * have a text/label for displaying text.
 * 
 * @author <a href="https://github.com/ogallagher">ogallagher</a>
 * @since 17 August 2021
 * 
 */
public class SimpleEdge extends AbstractEdge {
	/**
	 * Full {@link SimpleEdge} constructor.
	 * 
	 * @param source Source cell.
	 * @param target Target cell.
	 * @param isDirected Whether this edge is directed.
	 * 
	 * @implNote This skips {@link AbstractEdge#linkCells()}, assuming the cells in question are already child-parent
	 * linked by reference if needed, by calling {@link #linkCells()}.
	 */
	public SimpleEdge(ICell source, ICell target, boolean isDirected) {
		super(source, target, isDirected);
	}
	
	/**
	 * Does nothing, preventing default {@link AbstractEdge#linkCells()} behavior.
	 */
	@Override
	protected void linkCells() {}
	
	/**
	 * @return A newly generated {@link SimpleEdgeGraphic} instance.
	 */
	@Override
	public Region getGraphic(Graph graph) {
		return new SimpleEdgeGraphic(this, graph);
	}
	
	/**
	 * Class name and endpoint coordinates.
	 */
	@Override
	public String toString() {
		return "SimpleEdge(start=" + getSource() + ", end=" + getTarget() + ")";
	}
	
	/**
	 * Returns {@code true} if {@code other} is an {@link IEdge} instance, and if the start and end
	 * cells of this edge and the other are equal, or if neither are directed and the start and
	 * end cells are swapped.
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof IEdge) {
			IEdge edge = (IEdge) other;
			ICell s = getSource();
			ICell t = getTarget();
			
			return 
				(s == edge.getSource() && t == edge.getTarget()) || 
				(!this.isDirected() && !edge.isDirected() && t == edge.getSource() && s == edge.getTarget());
		}
		else {
			return false;
		}
	}
	
	/**
	 * Graphic to represent a {@code SimpleEdge} in a graph canvas.
	 * 
	 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
	 * @author <a href="https://github.com/ogallagher">ogallagher</a>
	 * 
	 * @since 17 August 2021
	 *
	 */
	public static class SimpleEdgeGraphic extends AbstractEdgeGraphic {
		/**
		 * A unique style class for targeting simple edge graphics for css in the graph.
		 */
		public static final String UNIQUE_STYLE_CLASS = "simple-edge-graphic";
		
		/**
		 * {@link SimpleEdgeGraphic} constructor.
		 * 
		 * @param edge The associated edge.
		 * @param graph The graph to whose canvas this graphic will be added.
		 */
		public SimpleEdgeGraphic(SimpleEdge edge, Graph graph) {
			super();
			
			// define a unique style class
			getStyleClass().add(UNIQUE_STYLE_CLASS);
			
			// disable unused AbstractEdgeGraphic members
			text.setVisible(false);
			text.setManaged(false);
			group.setVisible(false);
			group.setManaged(false);
			
			// get start and end point bindings for the edge line
			final DoubleBinding sourceX = edge.getSource().getXAnchor(graph);
			final DoubleBinding sourceY = edge.getSource().getYAnchor(graph);
			final DoubleBinding targetX = edge.getTarget().getXAnchor(graph);
			final DoubleBinding targetY = edge.getTarget().getYAnchor(graph);
			
			if (edge.isDirected()) {
				// create directed line (arrow)
				Region target = graph.getGraphic(edge.getTarget());
				setupArrowIntersect(target, sourceX, sourceY, targetX, targetY);
			}
			else {
				// create undirected line
				Line line = new Line();
				line.startXProperty().bind(sourceX);
				line.startYProperty().bind(sourceY);
				line.endXProperty().bind(targetX);
				line.endYProperty().bind(targetY);
				
				// assign to arrow
				ObservableList<Node> arrowSegments = arrow.getChildren();
				arrowSegments.clear();
				arrowSegments.add(line);
			}
			
			// add arrow/line to pane children
			getChildren().add(arrow);
		}
	}
}
