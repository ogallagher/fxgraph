package com.fxgraph.graph;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.layout.Region;

import java.util.List;

/**
 * Interface that any graph cell must implement. A cell is a <i>hierarchical</i> graph node that
 * can have child cells and parent cells.
 * 
 * Each cell has references to both its children and its parents. However, note that a cell can add and remove children,
 * but can so far only <i>add</i> parents (not remove them).
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 *
 */
public interface ICell extends IGraphNode {
	/**
	 * Add a child cell.
	 * 
	 * @param cell The child cell.
	 */
	void addCellChild(ICell cell);

	/**
	 * @return List of child cells.
	 */
	List<ICell> getCellChildren();
	
	/**
	 * Add a parent cell.
	 * 
	 * @param cell The parent cell.
	 */
	void addCellParent(ICell cell);

	/**
	 * @return List of cell parents.
	 */
	List<ICell> getCellParents();

	/**
	 * Remove a child cell.
	 * 
	 * @param cell The child cell.
	 */
	void removeCellChild(ICell cell);
	
	/**
	 * Calculate the anchor point x-value for this cell, being the center of its graphic by default.
	 * 
	 * @param graph The graph that the cell is displayed in.
	 * @return The result of {@code DoubleProperty} arithmetic.
	 */
	default DoubleBinding getXAnchor(Graph graph) {
		final Region graphic = graph.getGraphic(this);
		return graphic.layoutXProperty().add(graphic.widthProperty().divide(2));
	}

	/**
	 * Calculate the anchor point y-value for this cell, being the center of its graphic by default.
	 * 
	 * @param graph The graph that the cell is displayed in.
	 * @return The result of {@code DoubleProperty} arithmetic.
	 */
	default DoubleBinding getYAnchor(Graph graph) {
		final Region graphic = graph.getGraphic(this);
		return graphic.layoutYProperty().add(graphic.heightProperty().divide(2));
	}

	/**
	 * Calculate the width for this cell, being the width of its graphic by default.
	 * 
	 * @param graph The graph that the cell is displayed in.
	 * @return A read-only view of the cell graphic width property.
	 */
	default ReadOnlyDoubleProperty getWidth(Graph graph) {
		final Region graphic = graph.getGraphic(this);
		return graphic.widthProperty();
	}

	/**
	 * Calculate the height for this cell, being the height of its graphic by default.
	 * 
	 * @param graph The graph that the cell is displayed in.
	 * @return A read-only view of the cell graphic height property.
	 */
	default ReadOnlyDoubleProperty getHeight(Graph graph) {
		final Region graphic = graph.getGraphic(this);
		return graphic.heightProperty();
	}

}
