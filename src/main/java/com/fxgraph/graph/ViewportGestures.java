package com.fxgraph.graph;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * Listeners for making the scene's viewport draggable and zoomable.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 *
 */
public class ViewportGestures {
	/**
	 * How fast the scale changes when scrolling to zoom the viewport.
	 */
	private final DoubleProperty zoomSpeedProperty = new SimpleDoubleProperty(1.2d);
	/**
	 * Maximum viewport zoom.
	 */
	private final DoubleProperty maxScaleProperty = new SimpleDoubleProperty(10.0d);
	/**
	 * Minimum viewport zoom.
	 */
	private final DoubleProperty minScaleProperty = new SimpleDoubleProperty(0.1d);
	
	/**
	 * While a drag is happening (the mouse button has not yet been released), the drag context
	 * keeps track of the anchor point and the drag offset from the anchor.
	 */
	private final PannableCanvas.DragContext sceneDragContext = new PannableCanvas.DragContext();
	/**
	 * The graph whose viewport will be updated by these gestures.
	 */
	private final Graph graph;
	/**
	 * The {@link Graph graph's} canvas on which the graph node graphics are displayed.
	 */
	private final PannableCanvas canvas;
	/**
	 * The mouse button whose drag will update the viewport translation/panning.
	 */
	private MouseButton panButton = MouseButton.PRIMARY;
	
	/**
	 * @param graph The graph whose viewport will be updated by these gestures.
	 */
	public ViewportGestures(Graph graph) {
		this.graph = graph;
		this.canvas = graph.getCanvas();
	}

	/**
	 * @return The mouse button used for panning the canvas viewport on a drag.
	 */
	public MouseButton getPanButton() {
		return panButton;
	}

	/**
	 * @param panButton The mouse button to use for panning the canvas viewport on a drag.
	 */
	public void setPanButton(MouseButton panButton) {
		this.panButton = panButton;
	}

	/**
	 * @return The mouse pressed event handler.
	 */
	public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
		return onMousePressedEventHandler;
	}

	/**
	 * @return The mouse dragged event handler.
	 */
	public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
		return onMouseDraggedEventHandler;
	}

	/**
	 * @return The mouse released event handler.
	 */
	public EventHandler<MouseEvent> getOnMouseReleasedEventHandler() {
		return onMouseReleasedEventHandler;
	}

	/**
	 * @return The scroll event handler.
	 */
	public EventHandler<ScrollEvent> getOnScrollEventHandler() {
		return onScrollEventHandler;
	}

	/**
	 * Set minimum and maximum zoom/scale levels.
	 * 
	 * @param minScale Minimum scale. Default = {@code 0.1}
	 * @param maxScale Maximum scale. Default = {@code 10.0}
	 */
	public void setZoomBounds(double minScale, double maxScale) {
		minScaleProperty.set(minScale);
		maxScaleProperty.set(maxScale);
	}

	/**
	 * If a mouse button is pressed, potentially begin a new pan drag.
	 */
	private final EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			// target mouse button => panning
			if(event.getButton() == getPanButton()) {
				sceneDragContext.mouseAnchorX = event.getSceneX();
				sceneDragContext.mouseAnchorY = event.getSceneY();

				sceneDragContext.translateAnchorX = canvas.getTranslateX();
				sceneDragContext.translateAnchorY = canvas.getTranslateY();

				event.consume();
			}
		}
	};

	/**
	 * If the mouse is moved while pressed (drag), potentially update a pan drag.
	 */
	private final EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			// target mouse button => panning
			if(event.getButton() == getPanButton()) {
				canvas.setTranslateX(sceneDragContext.translateAnchorX + event.getSceneX() - sceneDragContext.mouseAnchorX);
				canvas.setTranslateY(sceneDragContext.translateAnchorY + event.getSceneY() - sceneDragContext.mouseAnchorY);

				event.consume();
			}
		}
	};

	/**
	 * If a mouse button is released, potentially finish a pan drag.
	 */
	private final EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			// target mouse button => panning
			if(event.getButton() == getPanButton()) {
				graph.getNodeGestures().revertLastNodeTransparency();
			}
		}
	};

	/**
	 * Mouse wheel handler: zoom to pivot point. The pivot point is the cursor's viewport coordinates, relative
	 * to which all graph nodes are scaled.
	 */
	private final EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent event) {
			double scale = canvas.getScale(); // currently we only use Y, same value is used for X
			final double oldScale = scale;

			if(event.getDeltaY() < 0) {
				scale /= getZoomSpeed();
			} else if (event.getDeltaY() > 0) {
				scale *= getZoomSpeed();
			}

			scale = clamp(scale, minScaleProperty.get(), maxScaleProperty.get());
			final double f = (scale / oldScale) - 1;

			// maxX = right overhang, maxY = lower overhang
			final double maxX = canvas.getBoundsInParent().getMaxX() - canvas.localToParent(canvas.getPrefWidth(), canvas.getPrefHeight()).getX();
			final double maxY = canvas.getBoundsInParent().getMaxY() - canvas.localToParent(canvas.getPrefWidth(), canvas.getPrefHeight()).getY();

			// minX = left overhang, minY = upper overhang
			final double minX = canvas.localToParent(0, 0).getX() - canvas.getBoundsInParent().getMinX();
			final double minY = canvas.localToParent(0, 0).getY() - canvas.getBoundsInParent().getMinY();

			// adding the overhangs together, as we only consider the width of canvas itself
			final double subX = maxX + minX;
			final double subY = maxY + minY;

			// subtracting the overall overhang from the width and only the left and upper overhang from the upper left point
			final double dx = (event.getSceneX() - ((canvas.getBoundsInParent().getWidth() - subX) / 2 + (canvas.getBoundsInParent().getMinX() + minX)));
			final double dy = (event.getSceneY() - ((canvas.getBoundsInParent().getHeight() - subY) / 2 + (canvas.getBoundsInParent().getMinY() + minY)));

			canvas.setScale(scale);

			// note: pivot value must be untransformed, i. e. without scaling
			canvas.setPivot(f * dx, f * dy);

			// prevent scroll event from propagating to other widgets
			event.consume();
		}
	};

	/**
	 * Limit/clamp a value between a minimum and maximum.
	 * 
	 * @param value The value to clamp.
	 * @param min Minimum value; anything below becomes {@code min}.
	 * @param max Maximum value; anything above becomes {@code max}.
	 * 
	 * @return The clamped value.
	 */
	public static double clamp(double value, double min, double max) {
		if(Double.compare(value, min) < 0) {
			return min;
		}

		if(Double.compare(value, max) > 0) {
			return max;
		}

		return value;
	}

	public double getMinScale() {
		return minScaleProperty.get();
	}

	public void setMinScale(double minScale) {
		minScaleProperty.set(minScale);
	}

	public DoubleProperty minScaleProperty() {
		return minScaleProperty;
	}

	public double getMaxScale() {
		return maxScaleProperty.get();
	}

	public DoubleProperty maxScaleProperty() {
		return maxScaleProperty;
	}

	public void setMaxScale(double maxScale) {
		maxScaleProperty.set(maxScale);
	}

	public double getZoomSpeed() {
		return zoomSpeedProperty.get();
	}

	public DoubleProperty zoomSpeedProperty() {
		return zoomSpeedProperty;
	}

	public void setZoomSpeed(double zoomSpeed) {
		zoomSpeedProperty.set(zoomSpeed);
	}
}