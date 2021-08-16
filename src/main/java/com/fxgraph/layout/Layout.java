package com.fxgraph.layout;

import com.fxgraph.graph.Graph;

/**
 * An implementing class defines how graph nodes will be placed spatially. A graph node doesn't
 * define where it will be, only what kind of node it is and what it's connected to.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 *
 */
public interface Layout {
	/**
	 * Determine locations within the 2d graph for all graph nodes.
	 * 
	 * @param graph
	 */
	public void execute(Graph graph);
}