package com.fxgraph.cells;

import com.fxgraph.graph.Graph;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles cell resize handles (bounding rect corners and edge midpoints) display and interaction.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a>
 *
 */
public class CellGestures {
	/**
	 * The radius of the cell drag handle (half of the width).
	 */
	static final double handleRadius = 6d;
	
	/**
	 * Create a cell drag handle at the north edge, handling mouse events and creating the handle
	 * widget to be displayed.
	 */
	static DragNodeSupplier NORTH = new DragNodeSupplier() {
		@Override
		public Node apply(Graph graph, Region region, Wrapper<MouseEvent> wrappedEvent) {
			final DoubleProperty xProperty = region.layoutXProperty();
			final DoubleProperty yProperty = region.layoutYProperty();
			final ReadOnlyDoubleProperty widthProperty = region.prefWidthProperty();
			final DoubleBinding halfWidthProperty = widthProperty.divide(2);

			final Rectangle resizeHandleN = new Rectangle(handleRadius, handleRadius, Color.BLACK);
			resizeHandleN.xProperty().bind(xProperty.add(halfWidthProperty).subtract(handleRadius / 2));
			resizeHandleN.yProperty().bind(yProperty.subtract(handleRadius / 2));

			setUpDragging(graph, resizeHandleN, wrappedEvent, Cursor.N_RESIZE);

			resizeHandleN.setOnMouseDragged(event -> {
				// [ogallagher] button need not be checked again, as setUpDragging ensures wrapped event value 
				//   not null if button is correct.
				if(wrappedEvent.value != null) {
					dragNorth(event, wrappedEvent, region, handleRadius);
					wrappedEvent.value = event;
					// [ogallagher] prevent drag event from propagating elsewhere
					event.consume();
				}
			});
			return resizeHandleN;
		}
	};
	
	/**
	 * Create a cell drag handle at the northeast corner, handling mouse events and creating the handle
	 * widget to be displayed.
	 */
	static DragNodeSupplier NORTH_EAST = new DragNodeSupplier() {
		@Override
		public Node apply(Graph graph, Region region, Wrapper<MouseEvent> wrappedEvent) {
			final DoubleProperty xProperty = region.layoutXProperty();
			final DoubleProperty yProperty = region.layoutYProperty();
			final ReadOnlyDoubleProperty widthProperty = region.prefWidthProperty();

			final Rectangle resizeHandleNE = new Rectangle(handleRadius, handleRadius, Color.BLACK);
			resizeHandleNE.xProperty().bind(xProperty.add(widthProperty).subtract(handleRadius / 2));
			resizeHandleNE.yProperty().bind(yProperty.subtract(handleRadius / 2));

			setUpDragging(graph, resizeHandleNE, wrappedEvent, Cursor.NE_RESIZE);

			resizeHandleNE.setOnMouseDragged(event -> {
				// [ogallagher] button need not be checked again, as setUpDragging ensures wrapped event value 
				//   not null if button is correct.
				if(wrappedEvent.value != null) {
					dragNorth(event, wrappedEvent, region, handleRadius);
					dragEast(event, wrappedEvent, region, handleRadius);
					wrappedEvent.value = event;
					// [ogallagher] prevent drag event from propagating elsewhere
					event.consume();
				}
			});
			return resizeHandleNE;
		}
	};
	
	/**
	 * Create a cell drag handle at the east edge, handling mouse events and creating the handle
	 * widget to be displayed.
	 */
	static DragNodeSupplier EAST = new DragNodeSupplier() {
		@Override
		public Node apply(Graph graph, Region region, Wrapper<MouseEvent> wrappedEvent) {
			final DoubleProperty xProperty = region.layoutXProperty();
			final DoubleProperty yProperty = region.layoutYProperty();
			final ReadOnlyDoubleProperty widthProperty = region.prefWidthProperty();
			final ReadOnlyDoubleProperty heightProperty = region.prefHeightProperty();
			final DoubleBinding halfHeightProperty = heightProperty.divide(2);

			final Rectangle resizeHandleE = new Rectangle(handleRadius, handleRadius, Color.BLACK);
			resizeHandleE.xProperty().bind(xProperty.add(widthProperty).subtract(handleRadius / 2));
			resizeHandleE.yProperty().bind(yProperty.add(halfHeightProperty).subtract(handleRadius / 2));

			setUpDragging(graph, resizeHandleE, wrappedEvent, Cursor.E_RESIZE);

			resizeHandleE.setOnMouseDragged(event -> {
				// [ogallagher] button need not be checked again, as setUpDragging ensures wrapped event value 
				//   not null if button is correct.
				if(wrappedEvent.value != null) {
					dragEast(event, wrappedEvent, region, handleRadius);
					wrappedEvent.value = event;
					// [ogallagher] prevent drag event from propagating elsewhere
					event.consume();
				}
			});
			return resizeHandleE;
		}
	};
	
	/**
	 * Create a cell drag handle at the southeast corner, handling mouse events and creating the handle
	 * widget to be displayed.
	 */
	static DragNodeSupplier SOUTH_EAST = new DragNodeSupplier() {
		@Override
		public Node apply(Graph graph, Region region, Wrapper<MouseEvent> wrappedEvent) {
			final DoubleProperty xProperty = region.layoutXProperty();
			final DoubleProperty yProperty = region.layoutYProperty();
			final ReadOnlyDoubleProperty widthProperty = region.prefWidthProperty();
			final ReadOnlyDoubleProperty heightProperty = region.prefHeightProperty();

			final Rectangle resizeHandleSE = new Rectangle(handleRadius, handleRadius, Color.BLACK);
			resizeHandleSE.xProperty().bind(xProperty.add(widthProperty).subtract(handleRadius / 2));
			resizeHandleSE.yProperty().bind(yProperty.add(heightProperty).subtract(handleRadius / 2));

			setUpDragging(graph, resizeHandleSE, wrappedEvent, Cursor.SE_RESIZE);

			resizeHandleSE.setOnMouseDragged(event -> {
				// [ogallagher] button need not be checked again, as setUpDragging ensures wrapped event value 
				//   not null if button is correct.
				if(wrappedEvent.value != null) {
					dragSouth(event, wrappedEvent, region, handleRadius);
					dragEast(event, wrappedEvent, region, handleRadius);
					wrappedEvent.value = event;
					// [ogallagher] prevent drag event from propagating elsewhere
					event.consume();
				}
			});
			return resizeHandleSE;
		}
	};
	
	/**
	 * Create a cell drag handle at the south edge, handling mouse events and creating the handle
	 * widget to be displayed.
	 */
	static DragNodeSupplier SOUTH = new DragNodeSupplier() {
		@Override
		public Node apply(Graph graph, Region region, Wrapper<MouseEvent> wrappedEvent) {
			final DoubleProperty xProperty = region.layoutXProperty();
			final DoubleProperty yProperty = region.layoutYProperty();
			final ReadOnlyDoubleProperty widthProperty = region.prefWidthProperty();
			final DoubleBinding halfWidthProperty = widthProperty.divide(2);
			final ReadOnlyDoubleProperty heightProperty = region.prefHeightProperty();

			final Rectangle resizeHandleS = new Rectangle(handleRadius, handleRadius, Color.BLACK);
			resizeHandleS.xProperty().bind(xProperty.add(halfWidthProperty).subtract(handleRadius / 2));
			resizeHandleS.yProperty().bind(yProperty.add(heightProperty).subtract(handleRadius / 2));

			setUpDragging(graph, resizeHandleS, wrappedEvent, Cursor.S_RESIZE);

			resizeHandleS.setOnMouseDragged(event -> {
				if(wrappedEvent.value != null) {
					dragSouth(event, wrappedEvent, region, handleRadius);
					wrappedEvent.value = event;
					// [ogallagher] prevent drag event from propagating elsewhere
					event.consume();
				}
			});
			return resizeHandleS;
		}
	};
	
	/**
	 * Create a cell drag handle at the southwest corner, handling mouse events and creating the handle
	 * widget to be displayed.
	 */
	static DragNodeSupplier SOUTH_WEST = new DragNodeSupplier() {
		@Override
		public Node apply(Graph graph, Region region, Wrapper<MouseEvent> wrappedEvent) {
			final DoubleProperty xProperty = region.layoutXProperty();
			final DoubleProperty yProperty = region.layoutYProperty();
			final ReadOnlyDoubleProperty heightProperty = region.prefHeightProperty();

			final Rectangle resizeHandleSW = new Rectangle(handleRadius, handleRadius, Color.BLACK);
			resizeHandleSW.xProperty().bind(xProperty.subtract(handleRadius / 2));
			resizeHandleSW.yProperty().bind(yProperty.add(heightProperty).subtract(handleRadius / 2));

			setUpDragging(graph, resizeHandleSW, wrappedEvent, Cursor.SW_RESIZE);

			resizeHandleSW.setOnMouseDragged(event -> {
				// [ogallagher] button need not be checked again, as setUpDragging ensures wrapped event value 
				//   not null if button is correct.
				if(wrappedEvent.value != null) {
					dragSouth(event, wrappedEvent, region, handleRadius);
					dragWest(event, wrappedEvent, region, handleRadius);
					wrappedEvent.value = event;
					// [ogallagher] prevent drag event from propagating elsewhere
					event.consume();
				}
			});
			return resizeHandleSW;
		}
	};
	
	/**
	 * Create a cell drag handle at the west edge, handling mouse events and creating the handle
	 * widget to be displayed.
	 */
	static DragNodeSupplier WEST = new DragNodeSupplier() {
		@Override
		public Node apply(Graph graph, Region region, Wrapper<MouseEvent> wrappedEvent) {
			final DoubleProperty xProperty = region.layoutXProperty();
			final DoubleProperty yProperty = region.layoutYProperty();
			final ReadOnlyDoubleProperty heightProperty = region.prefHeightProperty();
			final DoubleBinding halfHeightProperty = heightProperty.divide(2);

			final Rectangle resizeHandleW = new Rectangle(handleRadius, handleRadius, Color.BLACK);
			resizeHandleW.xProperty().bind(xProperty.subtract(handleRadius / 2));
			resizeHandleW.yProperty().bind(yProperty.add(halfHeightProperty).subtract(handleRadius / 2));

			setUpDragging(graph, resizeHandleW, wrappedEvent, Cursor.W_RESIZE);

			resizeHandleW.setOnMouseDragged(event -> {
				// [ogallagher] button need not be checked again, as setUpDragging ensures wrapped event value 
				//   not null if button is correct.
				if(wrappedEvent.value != null) {
					dragWest(event, wrappedEvent, region, handleRadius);
					wrappedEvent.value = event;
					// [ogallagher] prevent drag event from propagating elsewhere
					event.consume();
				}
			});
			return resizeHandleW;
		}
	};
	
	/**
	 * Create a cell drag handle at the northwest corner, handling mouse events and creating the handle
	 * widget to be displayed.
	 */
	static DragNodeSupplier NORTH_WEST = new DragNodeSupplier() {
		@Override
		public Node apply(Graph graph, Region region, Wrapper<MouseEvent> wrappedEvent) {
			final DoubleProperty xProperty = region.layoutXProperty();
			final DoubleProperty yProperty = region.layoutYProperty();

			final Rectangle resizeHandleNW = new Rectangle(handleRadius, handleRadius, Color.BLACK);
			resizeHandleNW.xProperty().bind(xProperty.subtract(handleRadius / 2));
			resizeHandleNW.yProperty().bind(yProperty.subtract(handleRadius / 2));

			setUpDragging(graph, resizeHandleNW, wrappedEvent, Cursor.NW_RESIZE);

			resizeHandleNW.setOnMouseDragged(event -> {
				// [ogallagher] button need not be checked again, as setUpDragging ensures wrapped event value 
				//   not null if button is correct.
				if(wrappedEvent.value != null) {
					dragNorth(event, wrappedEvent, region, handleRadius);
					dragWest(event, wrappedEvent, region, handleRadius);
					wrappedEvent.value = event;
					// [ogallagher] prevent drag event from propagating elsewhere
					event.consume();
				}
			});
			return resizeHandleNW;
		}
	};
	
	/**
	 * Convenience method for calling {@link #makeResizable(Graph, Region, DragNodeSupplier...)} with {@code DragNodeSupplier}s
	 * for all corners and edges.
	 * 
	 * @param graph Graph containing the cell.
	 * @param region Cell graphic.
	 */
	public static void makeResizable(Graph graph, Region region) {
		makeResizable(graph, region, NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST);
	}
	
	/**
	 * Synchronizes cell drag handles with their associated cell, ensuring that the drag handles belong to the same
	 * graph canvas as the their cell.
	 * 
	 * @param graph
	 * @param region
	 * @param nodeSuppliers
	 */
	public static void makeResizable(Graph graph, Region region, DragNodeSupplier... nodeSuppliers) {
		final Wrapper<MouseEvent> wrappedEvent = new Wrapper<>();
		
		// apply each drag node and obtain the list of their widgets
		final List<Node> dragNodes = Arrays.stream(nodeSuppliers)
			.map(supplier -> supplier.apply(graph, region, wrappedEvent))
			.collect(Collectors.toList());
		
		// ensure that the drag nodes belong to the same parent as their cell
		region.parentProperty().addListener((obs, oldParent, newParent) -> {
			for(final Node c : dragNodes) {
				final Pane currentParent = (Pane) c.getParent();
				if(currentParent != null) {
					currentParent.getChildren().remove(c);
				}
				((Pane) newParent).getChildren().add(c);
			}
		});
	}
	
	private static void dragNorth(MouseEvent event, Wrapper<MouseEvent> wrappedEvent, Region region, double handleRadius) {
		final double deltaY = event.getSceneY() - wrappedEvent.value.getSceneY();
		final double newY = region.getLayoutY() + deltaY;
		if(newY != 0 && newY >= handleRadius && newY <= region.getLayoutY() + region.getHeight() - handleRadius) {
			region.setLayoutY(newY);
			region.setPrefHeight(region.getPrefHeight() - deltaY);
		}
	}

	private static void dragEast(MouseEvent event, Wrapper<MouseEvent> wrappedEvent, Region region, double handleRadius) {
		final double deltaX = event.getSceneX() - wrappedEvent.value.getSceneX();
		final double newMaxX = region.getLayoutX() + region.getWidth() + deltaX;
		if(newMaxX >= region.getLayoutX() && newMaxX <= region.getParent().getBoundsInLocal().getWidth() - handleRadius) {
			region.setPrefWidth(region.getPrefWidth() + deltaX);
		}
	}
	
	private static void dragSouth(MouseEvent event, Wrapper<MouseEvent> wrappedEvent, Region region, double handleRadius) {
		final double deltaY = event.getSceneY() - wrappedEvent.value.getSceneY();
		final double newMaxY = region.getLayoutY() + region.getHeight() + deltaY;
		if(newMaxY >= region.getLayoutY() && newMaxY <= region.getParent().getBoundsInLocal().getHeight() - handleRadius) {
			region.setPrefHeight(region.getPrefHeight() + deltaY);
		}
	}
	
	private static void dragWest(MouseEvent event, Wrapper<MouseEvent> wrappedEvent, Region region, double handleRadius) {
		final double deltaX = event.getSceneX() - wrappedEvent.value.getSceneX();
		final double newX = region.getLayoutX() + deltaX;
		if(newX != 0 && newX <= region.getParent().getBoundsInLocal().getWidth() - handleRadius) {
			region.setLayoutX(newX);
			region.setPrefWidth(region.getPrefWidth() - deltaX);
		}
	}
	
	/**
	 * Extracts common mouse event handling shared by many cell resize handles. This takes care of changing the cursor,
	 * beginning a cell handle drag, and ending a drag.
	 * 
	 * @param graph Graph to which the cell belongs.
	 * @param node The cell handle.
	 * @param wrappedEvent The wrapped mouse event.
	 * @param hoverCursor The cursor to use when hovering over this cell handle.
	 */
	private static void setUpDragging(Graph graph, Node node, Wrapper<MouseEvent> wrappedEvent, Cursor hoverCursor) {
		node.setOnMouseEntered(event -> {
			node.getParent().setCursor(hoverCursor);
		});
		
		node.setOnMouseExited(event -> {
			node.getParent().setCursor(Cursor.DEFAULT);
		});
		
		node.setOnMousePressed(event -> {
			if (event.getButton().equals(graph.getNodeGestures().getDragButton())) {
				// begin a drag
				node.getParent().setCursor(Cursor.CLOSED_HAND);
				wrappedEvent.value = event;
				// don't allow partial external interaction if a cell handle is pressed
				event.consume();
			}
			else {
				// disable interaction on cell handle if wrong button
				wrappedEvent.value = null;
			}
		});
		
		node.setOnMouseReleased(event -> {
			// end a drag
			node.getParent().setCursor(Cursor.DEFAULT);
			wrappedEvent.value = null;
		});
	}
	
	/**
	 * TODO [ogallagher] document this class; I don't understand its purpose. It seems to have to do with tracking
	 * the drag start point, and whether the drag started or not.
	 * 
	 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
	 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
	 *
	 */
	static class Wrapper<T> {
		/**
		 * The {@link MouseEvent} that begins a cell handle drag.
		 */
		T value;
	}
	
	/**
	 * Any draggable cell handle must implement this interface. 
	 * 
	 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
	 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
	 *
	 */
	interface DragNodeSupplier {
		/**
		 * Defines how to apply a mouse event to a graph node cell.
		 * 
		 * @param graph The graph to which the cell belongs.
		 * @param region The cell's graphic (gui representation).
		 * @param wrappedEvent A wrapped {@link MouseEvent} instance.
		 * @return
		 */
		Node apply(Graph graph, Region region, Wrapper<MouseEvent> wrappedEvent);
	}

}
