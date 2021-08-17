package com.fxgraph.cells;

import com.fxgraph.graph.ICell;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract superclass with default {@link ICell} implementation, simply defining
 * managed lists of parent and child cells.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 *
 */
public abstract class AbstractCell implements ICell {
	/**
	 * Child cells.
	 */
	private final List<ICell> children = new ArrayList<>();
	/**
	 * Parent cells.
	 */
	private final List<ICell> parents = new ArrayList<>();
	
	@Override
	public void addCellChild(ICell cell) {
		children.add(cell);
	}

	@Override
	public List<ICell> getCellChildren() {
		return children;
	}

	@Override
	public void addCellParent(ICell cell) {
		parents.add(cell);
	}

	@Override
	public List<ICell> getCellParents() {
		return parents;
	}

	@Override
	public void removeCellChild(ICell cell) {
		children.remove(cell);
	}
}
