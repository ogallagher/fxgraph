package com.fxgraph.layout;

import java.awt.geom.Rectangle2D;

import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.graph.PannableCanvas;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.layout.Region;

/**
 * Modifies the graph canvas viewport so all graph nodes are visible.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a>
 * @since 2021-08-21
 */
public class FitToContentLayout implements Layout {
	/**
	 * Whether to invert the y axis by making y positive up and shifting the origin to the bottom left
	 * corner of the viewport. 
	 */
	private BooleanProperty invertY;
	/**
	 * Padding to add include between the borders and the content. 
	 */
	private Insets padding;
	
	public FitToContentLayout(boolean invertY, int padTop, int padRight, int padBottom, int padLeft) {
		this.invertY = new SimpleBooleanProperty(invertY);
		this.padding = new Insets(padTop, padRight, padBottom, padLeft);
	}
	
	/**
	 * Convenience constructor for {@link #FitToContentLayout(boolean)}, with {@code invertY=true}.
	 */
	public FitToContentLayout() {
		this(true, 0, 0, 0, 0);
	}
	
	/**
	 * Fit the graph content to the current canvas. Note that this will not work properly until <b>after</b> the
	 * graphics have been rendered.<br><br>
	 * 
	 * <b>To Do:</> Fix translation; scaling the canvas invalidates the calculated translation, and recalculating translation
	 * doesn't account for the scaling either :L
	 */
	@Override
	public void execute(Graph graph) {
		System.out.println("FitToContentLayout.execute");
		
		// measure content for scaling
		Rectangle2D contentSpace = measureContent(graph);
		
		PannableCanvas canvas = graph.getCanvas();
		
		// determine scale
		double cw = contentSpace.getWidth();
		double ch = contentSpace.getHeight();
		double scaleY = (canvas.getHeight() - padding.getTop() - padding.getBottom()) / ch;
		double scaleX = (canvas.getWidth() - padding.getRight() - padding.getLeft()) / cw;
		double scale;
		if (scaleY < scaleX) {
			scale = scaleY;
		}
		else {
			scale = scaleX;
		}
		canvas.setScale(scale);
		
		// TODO flip y axis
		
		// TODO determine translation
		double translateX = contentSpace.getMinX() - padding.getLeft();
		double translateY = contentSpace.getMinY() - padding.getTop();
		
		// transform graph canvas to fit content space (not working)
		
		canvas.setTranslateX(translateX);
		canvas.setTranslateY(translateY);
	}
	
	private Rectangle2D measureContent(Graph graph) {
		// determine current display space for content
		Rectangle2D contentSpace = new Rectangle2D.Double();
		
		Region graphic;
		Bounds graphicBounds;
		for (ICell cell : graph.getModel().getAllCells()) {
			graphic = graph.getGraphic(cell);
			graphicBounds = graphic.getBoundsInParent();
			
			// include graphic in content space
			contentSpace.add(new Rectangle2D.Double(
				graphicBounds.getMinX(), graphicBounds.getMinY(), 
				graphicBounds.getWidth(), graphicBounds.getHeight()
			));
		}
		
		return contentSpace;
	}
}
