package com.fxgraph.edges;

import com.fxgraph.graph.ICell;
import com.fxgraph.graph.IEdge;

/**
 * Abstract edge superclass with basic implementations of {@link IEdge} methods.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 * 
 * @implNote An abstract edge <i>links</i> connected cells together by updating cell references so
 * that the source is the child cell, and the target is the parent cell.
 *
 */
public abstract class AbstractEdge implements IEdge {
	/**
	 * Source cell.
	 */
	private final ICell source;
	/**
	 * Target cell.
	 */
	private final ICell target;
	/**
	 * Whether this edge is directed.
	 */
	private final boolean isDirected;

	/**
	 * {@link AbstractEdge} constructor. Creation of this edge also ultimately calls {@link #linkCells()}.
	 * 
	 * @param source Source cell.
	 * @param target Target cell.
	 * @param isDirected Whether the edge is directed.
	 * 
	 * @throws NullPointerException if {@code source} or {@code target} is {@code null}.
	 */
	public AbstractEdge(ICell source, ICell target, boolean isDirected) {
		this.source = source;
		this.target = target;
		this.isDirected = isDirected;

		if (source == null) {
			throw new NullPointerException("Source cannot be null");
		}
		if (target == null) {
			throw new NullPointerException("Target cannot be null");
		}

		linkCells();
	}
	
	/**
	 * Create mutual references between connected cells, such that the <b>source</b> is the <b>child</b>, and
	 * the <b>target</b> is the <b>parent</b>.
	 */
	protected void linkCells() {
		source.addCellParent(target);
		target.addCellChild(source);
	}
	
	@Override
	public ICell getSource() {
		return source;
	}

	@Override
	public ICell getTarget() {
		return target;
	}

	@Override
	public boolean isDirected() {
		return isDirected;
	}
}
