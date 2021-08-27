package com.fxgraph.graph;

import com.fxgraph.layout.Layout;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates a 2d graph model (graph nodes: cells, edges) and its presentation onto a specialized javafx pane.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 *
 */
public class Graph {
	/**
	 * Managed collection of cells and edges to be shown in the graph. 
	 */
	private final Model model;
	/**
	 * The canvas on which graph node graphics are displayed (and interactive).
	 */
	private final PannableCanvas pannableCanvas;
	/**
	 * Maintains a mapping of graph nodes to their corresponding graphics.
	 */
	private final Map<IGraphNode, Region> graphics;
	/**
	 * Node gestures (cell drag translation).
	 */
	private final NodeGestures nodeGestures;
	/**
	 * Viewport gestures (drag panning, scroll zoom).
	 */
	private final ViewportGestures viewportGestures;
	/**
	 * Whether to use {@link #nodeGestures}.
	 */
	private final BooleanProperty useNodeGestures;
	/**
	 * Whether to use {@link #viewportGestures}.
	 */
	private final BooleanProperty useViewportGestures;

	/**
	 * Convenience constructor for {@link #Graph(Model) Graph(new Model())}.
	 */
	public Graph() {
		this(new Model());
	}
	
	/**
	 * Defines a model to contain cells and edges, default drag-translate node
	 * gestures, and default drag-pan and scroll-zoom viewport gestures.
	 * 
	 * @param model The model which will contain references to included graph nodes (cells, edges).
	 */
	public Graph(Model model) {
		this.model = model;
		
		nodeGestures = new NodeGestures(this);
		useNodeGestures = new SimpleBooleanProperty(true);
		useNodeGestures.addListener((obs, oldVal, newVal) -> {
			if (newVal) {
				model.getAllCells().forEach(cell -> nodeGestures.makeDraggable(getGraphic(cell)));
			} else {
				model.getAllCells().forEach(cell -> nodeGestures.makeUndraggable(getGraphic(cell)));
			}
		});

		pannableCanvas = new PannableCanvas();
		viewportGestures = new ViewportGestures(this);
		useViewportGestures = new SimpleBooleanProperty(true);
		useViewportGestures.addListener((obs, oldVal, newVal) -> {
			final Parent parent = pannableCanvas.parentProperty().get();
			if (parent == null) {
				return;
			}
			if (newVal) {
				parent.addEventHandler(MouseEvent.MOUSE_PRESSED, viewportGestures.getOnMousePressedEventHandler());
				parent.addEventHandler(MouseEvent.MOUSE_DRAGGED, viewportGestures.getOnMouseDraggedEventHandler());
				parent.addEventHandler(MouseEvent.MOUSE_RELEASED, viewportGestures.getOnMouseReleasedEventHandler());
				parent.addEventHandler(ScrollEvent.ANY, viewportGestures.getOnScrollEventHandler());
			} else {
				parent.removeEventHandler(MouseEvent.MOUSE_PRESSED, viewportGestures.getOnMousePressedEventHandler());
				parent.removeEventHandler(MouseEvent.MOUSE_DRAGGED, viewportGestures.getOnMouseDraggedEventHandler());
				parent.removeEventHandler(MouseEvent.MOUSE_RELEASED, viewportGestures.getOnMouseReleasedEventHandler());
				parent.removeEventHandler(ScrollEvent.ANY, viewportGestures.getOnScrollEventHandler());
			}
		});
		pannableCanvas.parentProperty().addListener((obs, oldVal, newVal) -> {
			if (oldVal != null) {
				oldVal.removeEventHandler(MouseEvent.MOUSE_PRESSED, viewportGestures.getOnMousePressedEventHandler());
				oldVal.removeEventHandler(MouseEvent.MOUSE_DRAGGED, viewportGestures.getOnMouseDraggedEventHandler());
				oldVal.removeEventHandler(MouseEvent.MOUSE_RELEASED, viewportGestures.getOnMouseReleasedEventHandler());
				oldVal.removeEventHandler(ScrollEvent.ANY, viewportGestures.getOnScrollEventHandler());
			}
			if (newVal != null) {
				newVal.addEventHandler(MouseEvent.MOUSE_PRESSED, viewportGestures.getOnMousePressedEventHandler());
				newVal.addEventHandler(MouseEvent.MOUSE_DRAGGED, viewportGestures.getOnMouseDraggedEventHandler());
				newVal.addEventHandler(MouseEvent.MOUSE_RELEASED, viewportGestures.getOnMouseReleasedEventHandler());
				newVal.addEventHandler(ScrollEvent.ANY, viewportGestures.getOnScrollEventHandler());
			}
		});

		graphics = new HashMap<>();

		addEdges(getModel().getAllEdges());
		addCells(getModel().getAllCells());
	}

	/**
	 * @return the graph canvas that acts as the graph's gui pane.
	 */
	public PannableCanvas getCanvas() {
		return pannableCanvas;
	}

	/**
	 * @return the model that contains the graph node references.
	 */
	public Model getModel() {
		return model;
	}
	
	/**
	 * Clears all widgets from the graph canvas.
	 * <br><br>
	 * This method seems misleading, as an update to the graph shouldn't necessarily mean all current
	 * graph nodes be removed.
	 */
	public void beginUpdate() {
		getCanvas().getChildren().clear();
	}

	/**
	 * Add and remove the appropriate cells and edges as listed in the {@link Model model}. Then call
	 * {@link Model#endUpdate()}.
	 */
	public void endUpdate() {
		// add components to graph pane
		addEdges(model.getAddedEdges());
		addCells(model.getAddedCells());

		// remove components to graph pane
		removeEdges(model.getRemovedEdges());
		removeCells(model.getRemovedCells());

		// clean up the model
		getModel().endUpdate();
	}

	/**
	 * Add edge graphics to the graph canvas.
	 * 
	 * @param edges Edges to add.
	 */
	private void addEdges(List<IEdge> edges) {
		edges.forEach(edge -> {
			try {
				Region edgeGraphic = getGraphic(edge);
				getCanvas().getChildren().add(edgeGraphic);
				edge.onAddedToGraph(this, edgeGraphic);
			} catch (final Exception e) {
				throw new RuntimeException("failed to add " + edge, e);
			}
		});
	}

	/**
	 * Remove edge graphics from the graph canvas.
	 * 
	 * @param edges Edges to remove.
	 */
	private void removeEdges(List<IEdge> edges) {
		edges.forEach(edge -> {
			try {
				Region edgeGraphic = getGraphic(edge);
				getCanvas().getChildren().remove(edgeGraphic);
				edge.onRemovedFromGraph(this, edgeGraphic);
			} catch (final Exception e) {
				throw new RuntimeException("failed to remove " + edge, e);
			}
		});
	}

	/**
	 * Add cell graphics to the graph canvas.
	 * 
	 * @param cells Cells to add.
	 */
	private void addCells(List<ICell> cells) {
		cells.forEach(cell -> {
			try {
				Region cellGraphic = getGraphic(cell);
				getCanvas().getChildren().add(cellGraphic);
				if (useNodeGestures.get()) {
					nodeGestures.makeDraggable(cellGraphic);
				}
				cell.onAddedToGraph(this, cellGraphic);
			} catch (final Exception e) {
				throw new RuntimeException("failed to add " + cell, e);
			}
		});
	}

	/**
	 * Remove cell graphics from the graph canvas.
	 * 
	 * @param cells Cells to remove.
	 */
	private void removeCells(List<ICell> cells) {
		cells.forEach(cell -> {
			try {
				Region cellGraphic = getGraphic(cell);
				getCanvas().getChildren().remove(cellGraphic);
				cell.onRemovedFromGraph(this, cellGraphic);
			} catch (final Exception e) {
				throw new RuntimeException("failed to remove " + cell, e);
			}
		});
	}

	/**
	 * Get the corresponding graphic for the given node (cell, edge).
	 * 
	 * @param node The graph node.
	 * @return The node's graphic.
	 */
	public Region getGraphic(IGraphNode node) {
		try {
			if (!graphics.containsKey(node)) {
				graphics.put(node, createGraphic(node));
			}
			return graphics.get(node);
		} catch (final Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Create the graphic that will represent the graph node in the canvas.
	 * 
	 * @apiNote This should only be called once for each node in a graph, as the method does not
	 * recycle the same graphic object each time it's called.
	 * 
	 * @param node The graph node (cell, edge).
	 * @return The graphic.
	 */
	public Region createGraphic(IGraphNode node) {
		return node.getGraphic(this);
	}

	/**
	 * @return The canvas viewport's scale/zoom value.
	 */
	public double getScale() {
		return getCanvas().getScale();
	}

	/**
	 * Use the given layout to calculate placement for all contained graph nodes.
	 * 
	 * @param layout
	 */
	public void layout(Layout layout) {
		layout.execute(this);
	}

	/**
	 * @return {code NodeGestures} for the graph.
	 */
	public NodeGestures getNodeGestures() {
		return nodeGestures;
	}

	/**
	 * @return Whether to use node gestures.
	 */
	public BooleanProperty getUseNodeGestures() {
		return useNodeGestures;
	}

	/**
	 * @return {code ViewportGestures} for the graph.
	 */
	public ViewportGestures getViewportGestures() {
		return viewportGestures;
	}

	/**
	 * @return Whether to use viewport gestures.
	 */
	public BooleanProperty getUseViewportGestures() {
		return useViewportGestures;
	}
}