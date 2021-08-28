package com.fxgraph.graph;

import javafx.scene.paint.Color;
import java.util.Collection;
import java.util.Date;

import com.fxgraph.cells.CartesianPoint;
import com.fxgraph.cells.CartesianPoint.BulletType;
import com.fxgraph.edges.SimpleEdge;
import com.fxgraph.layout.FitToContentLayout;
import com.fxgraph.layout.Layout;

import javafx.geometry.Point2D;

/**
 * A specialized graph that plots data in the Cartesian x,y plane.<br><br>
 * 
 * A single cartesian graph can plot many datasets on the same set of axes.<br><br>
 * 
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 * @since 17 August 2021
 * 
 */
public class CartesianGraph extends Graph {
	/**
	 * Plot mode for determining how to display data points.
	 */
	private PlotMode plotMode;
	
	/**
	 * tbd
	 */
	private MultiplotModel cartesianModel;
	
	/**
	 * tbd
	 */
	private FitToContentLayout layout;
	
	public CartesianGraph(PlotMode plotMode, int width, int height) {
		// instantiate graph with a plotting model
		super(new MultiplotModel());
		
		this.getUseNodeGestures().set(false);
		this.getUseViewportGestures().set(false);
		
		cartesianModel = (MultiplotModel) this.getModel();
		
		// set cartesian-specific parameters
		this.plotMode = plotMode;
		
		getCanvas().setPrefSize(width, height);
		
		layout = new FitToContentLayout();
	}
	
	/**
	 * @return Generate a name for the dataset.
	 */
	private String generateDatasetName() {
		return "plot " + new Date().getTime();
	}
	
	/**
	 * Add a new dataset to the graph with given styling.
	 * 
	 * @param dataset The data points to add.
	 * @param name The identifying name for the dataset. If {@code null}, a name will be generated.
	 * @param pointRadius Point bullet radius.
	 * @param pointBullet Point bullet type.
	 * @param pointFill Point fill color.
	 * 
	 * @return The name of the dataset.
	 */
	public String addDataset(Collection<Point2D> dataset, String name, double pointRadius, BulletType pointBullet, Color pointFill) {
		if (name == null) {
			name = generateDatasetName();
		}
		
		// add data as points and edges to model
		cartesianModel.addPlot(name, dataset, pointRadius, pointBullet, pointFill);
		
		// commit canvas and model changes
		endUpdate();
		
		return name;
	}
	
	/**
	 * Convenience method for {@link #addDataset(Collection, String, double, BulletType, Color)} with default styling.
	 * 
	 * @param dataset Data points to add.
	 * @param name Name of the dataset.
	 * 
	 * @return Name of the dataset.
	 */
	public String addDataset(Collection<Point2D> dataset, String name) {
		return addDataset(dataset, name, CartesianPoint.RADIUS_DEFAULT, CartesianPoint.BULLET_TYPE_DEFAULT, CartesianPoint.FILL_COLOR_DEFAULT);
	}
	
	/**
	 * Add a new point to the graph with given styling.
	 * 
	 * @param point The point to add.
	 * 
	 * @param plotName The plot to add this point to. If {@code null}, the default plot will be used, or a new plot with
	 * a generated name if there are no plots available.
	 * 
	 * @param pointRadius Point bullet radius.
	 * @param pointBullet Point bullet type.
	 * @param pointFill Point fill color.
	 * 
	 * @return The {@link CartesianPoint} instance that was added.
	 */
	public CartesianPoint addPoint(Point2D point, String plotName, double pointRadius, BulletType pointBullet, Color pointFill) {
		if (plotName == null) {
			// get default plot name
			plotName = cartesianModel.getPlotDefault();
			
			if (plotName == null) {
				// get generated plot name
				plotName = generateDatasetName();
			}
		}
		
		CartesianPoint cpoint = cartesianModel.addPoint(plotName, point, pointRadius, pointBullet, pointFill);
		
		// commit canvas and model changes
		endUpdate();
		
		return cpoint;
	}
	
	/**
	 * Convenience method for {@link #addPoint(Point2D, String, double, BulletType, Color)} with default styling.
	 * 
	 * @param point Point to add.
	 * @param plotName Plot to add to.
	 * 
	 * @return The {@code CartesianPoint} instance that was added.
	 */
	public CartesianPoint addPoint(Point2D point, String plotName) {
		return addPoint(point, plotName, CartesianPoint.RADIUS_DEFAULT, CartesianPoint.BULLET_TYPE_DEFAULT, CartesianPoint.FILL_COLOR_DEFAULT);
	}
	
	/**
	 * @param point The point from which to create the edge.
	 * 
	 * @see MultiplotModel#addEdge(CartesianPoint)
	 */
	public SimpleEdge addEdge(CartesianPoint point) {
		SimpleEdge edge = cartesianModel.addEdge(point);
		
		// commit canvas and model changes
		endUpdate();
		
		return edge;
	}
	
	/**
	 * @param plotName The plot to which to add the edge.
	 * @return The added edge.
	 * 
	 * @see MultiplotModel#addLastEdge(String)
	 */
	public SimpleEdge addLastEdge(String plotName) {
		SimpleEdge edge = cartesianModel.addLastEdge(plotName);
		
		// commit canvas and model changes
		endUpdate();
		
		return edge;
	}
	
	/**
	 * Convenience method for {@link #addLastEdge(String) addLastEdge(null)}.
	 */
	public SimpleEdge addLastEdge() {
		return addLastEdge(null);
	}
	
	/**
	 * Given placement of points on the canvas, update the viewport so all data graphics are visible.<br><br>
	 * 
	 * Calls {@link FitToContentLayout#execute(Graph) layout.execute(this)}.
	 */
	public void layout() {
		layout.execute(this);
	}
	
	/**
	 * A {@code CartesianGraph} calls {@link #layout()} to use its contained layout implementation.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void layout(Layout layout) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("cartesian graph layout implementation is fixed");
	}
	
	/**
	 * {@code CartesianGraph} plot modes.
	 * 
	 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
	 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
	 *
	 */
	public static enum PlotMode {
		/**
		 * Points shown as a scatter plot.
		 */
		POINTS,
		/**
		 * Connected line segments shown as a line graph.
		 */
		LINES,
		/**
		 * Points are shown, as well as the lines connecting them.
		 */
		CONNECTED_POINTS;
	}
	
	/**
	 * An axis in a cartesian graph that can be either horizontal or vertical, representing the x or y axis. It remains
	 * fixed to an edge of the viewport and can display unit ticks and have a label.
	 * 
	 * <br><br><b>NOT YET IMPLEMENTED</b>
	 * 
	 * @author <a href="https://github.com/ogallagher">ogallagher</a>
	 */
	public static class CartesianAxis {
		// TODO here
	}
}
