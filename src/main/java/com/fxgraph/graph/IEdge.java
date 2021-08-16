package com.fxgraph.graph;

/**
 * Interface that any graph edge must implement.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 *
 */
public interface IEdge extends IGraphNode {
	/**
	 * @return The cell designated as the edge source/start point.
	 */
	ICell getSource();
	
	/**
	 * @return The cell designated as the edge target/end point.
	 */
	ICell getTarget();

	/**
	 * @return Whether the edge is directed (whether source and target are distinguishable).
	 */
	boolean isDirected();
}
