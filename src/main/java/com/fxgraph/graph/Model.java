package com.fxgraph.graph;

import com.fxgraph.cells.AbstractCell;
import com.fxgraph.edges.Edge;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Region;

import java.io.Serializable;
import java.util.List;

/**
 * Owns all cells and edges that can be displayed on a {@link Graph graph}.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 *
 */
public class Model implements Serializable {
	private static final long serialVersionUID = 172247271876446110L;
	
	/**
	 * Cells are hierarchical, with a single root cell that has no parent cell. By default,
	 * a model root is an invisible {@code AbstractCell} with no graphic.
	 */
	private final ICell root;

	/**
	 * References to all cells in the model.
	 */
	private ObservableList<ICell> allCells;
	/**
	 * Temporary references to newly added cells, which are cleared on {@link Model#merge()}.
	 */
	private transient ObservableList<ICell> addedCells;
	/**
	 * Temporary references to newly removed cells, which are cleared on {@link Model#merge()}.
	 */
	private transient ObservableList<ICell> removedCells;

	/**
	 * References to all edges in the model.
	 */
	private ObservableList<IEdge> allEdges;
	/**
	 * Temporary references to newly added edges, which are cleared on {@link Model#merge()}.
	 */
	private transient ObservableList<IEdge> addedEdges;
	/**
	 * Temporary references to newly removed edges, which are cleared on {@link Model#merge()}.
	 */
	private transient ObservableList<IEdge> removedEdges;

	/**
	 * {@link Model} constructor. Creates the empty root cell and calls {@link #clear()}.
	 */
	public Model() {
		root = new AbstractCell() {
			@Override
			public Region getGraphic(Graph graph) {
				return null;
			}
		};
		
		// clear model, create lists
		clear();
	}
	
	/**
	 * Set all graphic node (cells, edges) references to empty observable lists.
	 */
	public void clear() {
		allCells = FXCollections.observableArrayList();
		addedCells = FXCollections.observableArrayList();
		removedCells = FXCollections.observableArrayList();

		allEdges = FXCollections.observableArrayList();
		addedEdges = FXCollections.observableArrayList();
		removedEdges = FXCollections.observableArrayList();
	}

	/**
	 * @deprecated Redundant and unused way to clear lists of newly added graphic nodes.
	 */
	public void clearAddedLists() {
		addedCells.clear();
		addedEdges.clear();
	}

	/**
	 * Assuming the caller already added and removed the proper graph nodes from the canvas,
	 * this ensures all added cells have a parent, and all removed cells do not. Then,
	 * {@link #merge()} is called.
	 * 
	 * @see #attachOrphansToGraphParent(List)
	 * @see #disconnectFromGraphParent(List)
	 */
	public void endUpdate() {
		// every cell must have a parent, if it doesn't, then the graphParent is
		// the parent
		attachOrphansToGraphParent(getAddedCells());
		
		// remove reference to graphParent
		disconnectFromGraphParent(getRemovedCells());
		
		// merge added & removed nodes with all nodes
		merge();
	}

	/**
	 * @return {@link #addedCells}.
	 */
	public ObservableList<ICell> getAddedCells() {
		return addedCells;
	}

	/**
	 * @return {@link #removedCells}.
	 */
	public ObservableList<ICell> getRemovedCells() {
		return removedCells;
	}

	/**
	 * @return {@link #allCells}.
	 */
	public ObservableList<ICell> getAllCells() {
		return allCells;
	}

	/**
	 * @return {@link #addedEdges}.
	 */
	public ObservableList<IEdge> getAddedEdges() {
		return addedEdges;
	}

	/**
	 * @return {@link #removedEdges}.
	 */
	public ObservableList<IEdge> getRemovedEdges() {
		return removedEdges;
	}
	
	/**
	 * @return {@link #allEdges}.
	 */
	public ObservableList<IEdge> getAllEdges() {
		return allEdges;
	}

	/**
	 * Add a cell to the model.
	 * 
	 * @param cell The cell to add.
	 * @throws NullPointerException If {@code cell} is {@code null}.
	 */
	public void addCell(ICell cell) throws NullPointerException {
		if(cell == null) {
			throw new NullPointerException("Cannot add a null cell");
		}
		addedCells.add(cell);
	}
	
	/**
	 * Remove a cell from the model.
	 * 
	 * @param cell The cell to remove.
	 * @throws NullPointerException If {@code cell} is {@code null}.
	 */
	public void removeCell(ICell cell) {
		if(cell == null) {
			throw new NullPointerException("Cannot remove a null cell");
		}
		removedCells.add(cell);
	}

	/**
	 * Convenience method for {@link #addEdge(IEdge)}, adding an {@link Edge} instance.
	 * 
	 * @param sourceCell The edge source cell.
	 * @param targetCell The edge target cell.
	 */
	public void addEdge(ICell sourceCell, ICell targetCell) {
		final IEdge edge = new Edge(sourceCell, targetCell);
		addEdge(edge);
	}
	
	/**
	 * Add an edge to the model.
	 * 
	 * @param edge The edge to add.
	 * 
	 * @throws NullPointerException If {@code edge} is {@code null}.
	 */
	public void addEdge(IEdge edge) throws NullPointerException {
		if(edge == null) {
			throw new NullPointerException("Cannot add a null edge");
		}
		addedEdges.add(edge);
	}
	
	/**
	 * Remove an edge from the model.
	 * 
	 * @param edge
	 */
	public void removeEdge(IEdge edge) {
		if(edge == null) {
			throw new NullPointerException("Cannot remove a null edge");
		}
		removedEdges.add(edge);
	}

	/**
	 * Attach all cells which don't have a parent to the {@link #root root cell}.
	 *
	 * @param cellList
	 */
	public void attachOrphansToGraphParent(List<ICell> cellList) {
		for(final ICell cell : cellList) {
			if(cell.getCellParents().size() == 0) {
				root.addCellChild(cell);
			}
		}
	}

	/**
	 * Remove cell children from the {@link #root root cell}.
	 *
	 * @param cellList
	 */
	public void disconnectFromGraphParent(List<ICell> cellList) {
		for(final ICell cell : cellList) {
			root.removeCellChild(cell);
		}
	}

	/**
	 * @return The {@link #root root} cell.
	 */
	public ICell getRoot() {
		return root;
	}

	/**
	 * Add newly added cells and edges to the model, remove newly removed cells and edges from the model, 
	 * and clear the corresponding temporary reference lists.
	 */
	public void merge() {
		// cells
		allCells.addAll(addedCells);
		allCells.removeAll(removedCells);

		addedCells.clear();
		removedCells.clear();

		// edges
		allEdges.addAll(addedEdges);
		allEdges.removeAll(removedEdges);

		addedEdges.clear();
		removedEdges.clear();
	}
}