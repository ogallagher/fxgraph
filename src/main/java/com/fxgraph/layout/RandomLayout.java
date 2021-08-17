package com.fxgraph.layout;

import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;

import java.util.List;
import java.util.Random;

/**
 * A graph layout implementation that simply places each cell in a graph randomly within 
 * a 500x500 square.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 *
 */
public class RandomLayout implements Layout {
	/**
	 * Random number generator instance.
	 */
	private final Random rnd = new Random();

	@Override
	public void execute(Graph graph) {
		final List<ICell> cells = graph.getModel().getAllCells();

		for (final ICell cell : cells) {
			final double x = rnd.nextDouble() * 500;
			final double y = rnd.nextDouble() * 500;

			graph.getGraphic(cell).relocate(x, y);
		}
	}

}