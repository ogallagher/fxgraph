package com.fxgraph.graph;

import javafx.scene.layout.Region;

/**
 * An interface that anything must implement in order to be displayed in a {@link Graph}.
 * Edges and cells implement this interface.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 *
 */
public interface IGraphNode {
	/**
	 * Get the graphic that will represent the graph node when displayed.
	 * 
	 * @param graph The graph that this node is a member of.
	 * @return The fx node used to display the graph node.
	 */
	public Region getGraphic(Graph graph);
	/**
	 * Handler for this node being added to a graph.<br><br>
	 * 
	 * Calling of this method is delegated to {@code Graph.addCells}.
	 * 
	 * @param graph The graph to which the graph node will be added.
	 * @param region The fx node used to display the graph node.
	 */
	default void onAddedToGraph(Graph graph, Region region) {}
	/**
	 * Handler for when this node is removed from a graph.<br><br>
	 * 
	 * Calling of this method is delegated to {@code Graph.removeCells}.
	 * 
	 * @param graph The graph from which the graph node will be removed.
	 * @param region The fx node used to display the graph node.
	 */
	default void onRemovedFromGraph(Graph graph, Region region) {}

}
