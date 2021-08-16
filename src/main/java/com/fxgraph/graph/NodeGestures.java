package com.fxgraph.graph;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class NodeGestures {
	/**
	 * While a drag is happening (the mouse button has not yet been released), the drag context
	 * keeps track of the drag offset from the drag start point.
	 */
	private final DragContext dragContext = new DragContext();
	/**
	 * The graph to which interactive graph nodes belong.
	 */
	private final Graph graph;
	/**
	 * The mouse button to use for dragging graph nodes.
	 */
	private MouseButton dragButton = MouseButton.PRIMARY;
	/**
	 * If the mouse button for viewport panning is used to start a drag over a graph node, that graph node's
	 * interactivity is disabled until that drag is complete.
	 * <br><br>
	 * TODO [ogallagher] It seems preferable to prioritize moving the node if the node drag button is the same as
	 * the viewport drag button.
	 */
	private Node lastTransparentNode;

	/**
	 * {@link NodeGestures} constructor.
	 * 
	 * @param graph The graph to which interactive graph nodes belong.
	 */
	public NodeGestures(Graph graph) {
		this.graph = graph;
	}

	/**
	 * Set which mouse button enables a graph node translation with dragging.
	 * 
	 * @param dragButton The mouse button for dragging.
	 */
	public void setDragButton(MouseButton dragButton) {
		this.dragButton = dragButton;
	}

	/**
	 * @return Mouse button used for node dragging.
	 */
	public MouseButton getDragButton() {
		return dragButton;
	}

	/**
	 * Enable drag translation on an fx widget (usually a graph node).
	 * 
	 * @param node The node to make draggable.
	 */
	public void makeDraggable(final Node node) {
		node.setOnMousePressed(onMousePressedEventHandler);
		node.setOnMouseDragged(onMouseDraggedEventHandler);
	}

	/**
	 * Disable drag translation  on an fx widget (usually a graph node).
	 * 
	 * @param node The node to make undraggable.
	 */
	public void makeUndraggable(final Node node) {
		node.setOnMousePressed(null);
		node.setOnMouseDragged(null);
	}

	/**
	 * Convenience method for 
	 * {@link #setNodeMouseTransparency(Node, boolean) setNodeMouseTransparency(lastTransparentNode, false)}.
	 */
	public void revertLastNodeTransparency() {
		setNodeMouseTransparency(lastTransparentNode, false);
	}
	
	/**
	 * Enable or disable (transparent) interactive gestures on a node. 
	 * 
	 * @param node The node to change.
	 * @param value Whether the node is transparent (not interactive).
	 */
	private void setNodeMouseTransparency(Node node, boolean value) {
		if (node == null) {
			return;
		}
		node.setMouseTransparent(value);
		if (node instanceof Parent) {
			for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
				setNodeMouseTransparency(child, value);
			}
		}
	}

	/**
	 * Handle the start point of a new drag, or disable node dragging if the viewport pan button
	 * was pressed.
	 */
	final EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			final Node node = (Node) event.getSource();

			if (event.getButton() == graph.getViewportGestures().getPanButton()) {
				// make the node transparent to mouse events if it is the pan button clicked.
				// this allows the pan button to be used while over the node.
				// we will restore its transparency when the mouse button is released.
				lastTransparentNode = node;
				setNodeMouseTransparency(node, true);
			} 
			else {
				final double scale = graph.getScale();

				dragContext.x = node.getBoundsInParent().getMinX() * scale - event.getScreenX();
				dragContext.y = node.getBoundsInParent().getMinY() * scale - event.getScreenY();

				event.consume();
			}
		}
	};

	/**
	 * Handle the endpoint of an incomplete node drag.
	 */
	final EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			final Node node = (Node) event.getSource();

			if (event.getButton() == getDragButton()) {
				double offsetX = event.getScreenX() + dragContext.x;
				double offsetY = event.getScreenY() + dragContext.y;

				// adjust the offset in case we are zoomed
				final double scale = graph.getScale();

				offsetX /= scale;
				offsetY /= scale;

				node.relocate(offsetX, offsetY);

				// only consume if target button.
				// allows for "pass-through" of events to parent when not the target button.
				event.consume();
			}
		}
	};
	
	/**
	 * Stores x,y anchor/start point information for a graph node drag in progress.
	 * 
	 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
	 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
	 *
	 */
	public static class DragContext {
		/**
		 * Node drag anchor x.
		 */
		double x;
		/**
		 * Node drag anchor y.
		 */
		double y;
	}
}