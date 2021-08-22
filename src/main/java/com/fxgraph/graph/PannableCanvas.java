package com.fxgraph.graph;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;

/**
 * A specialized pane that displays graphics children in a dynamic viewport.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 *
 */
public class PannableCanvas extends Pane {
	/**
	 * Viewport scale (default is {@code 1.0}). Note that the canvas x and y scale properties will bind to this value
	 * to maintain <i>square</i> zoom.
	 */
	private final DoubleProperty scaleProperty;
	
	/**
	 * {@link PannableCanvas} default constructor.
	 */
	public PannableCanvas() {
		this(new SimpleDoubleProperty(1.0));
	}

	/**
	 * Fully specified {@link PannableCanvas} constructor.
	 * 
	 * @param scaleProperty Initial value for {@link #scaleProperty}.
	 */
	public PannableCanvas(DoubleProperty scaleProperty) {
		this.scaleProperty = scaleProperty;
		scaleXProperty().bind(scaleProperty);
		scaleYProperty().bind(scaleProperty);
	}

	/**
	 * @return Value of {@link #scaleProperty}.
	 */
	public double getScale() {
		return scaleProperty.get();
	}

	/**
	 * Update the canvas zoom/scale value.
	 * 
	 * @param scale New value for {@link #scaleProperty}.
	 */
	public void setScale(double scale) {
		scaleProperty.set(scale);
	}
	
	/**
	 * TODO [ogallagher] since this method is between the caller and the canvas property, I think
	 * it makes sense to return a {@link javafx.beans.property.ReadOnlyDoubleProperty} view of the property.
	 * 
	 * @return {@link #scaleProperty}.
	 */
	public DoubleProperty scaleProperty() {
		return scaleProperty;
	}
	
	/**
	 * Set new coordinates for the <i>pivot point</i>, in viewport coordinates(?), relative to which zooming happens.
	 * 
	 * @param x x component.
	 * @param y y component.
	 */
	public void setPivot(double x, double y) {
		setTranslateX(getTranslateX() - x);
		setTranslateY(getTranslateY() - y);
	}
	
	/**
	 * Mouse drag context used for scene and nodes. A drag is defined by a start point (anchor) and an offset from that
	 * start point (translate anchor, end point). The translate anchor can change until the drag is complete. 
	 */
	public static class DragContext {
		double mouseAnchorX;
		double mouseAnchorY;

		double translateAnchorX;
		double translateAnchorY;

	}
}

