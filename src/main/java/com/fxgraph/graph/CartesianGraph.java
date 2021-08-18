package com.fxgraph.graph;

import java.util.Collection;
import java.util.Date;

import com.fxgraph.cells.CartesianPoint;
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
	
	public CartesianGraph(PlotMode plotMode) {
		// instantiate graph with a plotting model
		super(new MultiplotModel());
		
		this.getUseNodeGestures().set(false);
		this.getUseViewportGestures().set(false);
		
		cartesianModel = (MultiplotModel) this.getModel();
		
		// set cartesian-specific parameters
		this.plotMode = plotMode;
		
		layout = new FitToContentLayout();
	}
	
	/**
	 * @return Generate a name for the dataset.
	 */
	private String generateDatasetName() {
		return "plot " + new Date().getTime();
	}
	
	/**
	 * Add a new dataset to the graph.
	 * 
	 * @param dataset The data points to add.
	 * @param name The identifying name for the dataset. If {@code null}, a name will be generated.
	 * 
	 * @return The name of the dataset.
	 */
	public String addDataset(Collection<Point2D> dataset, String name) {
		if (name == null) {
			name = generateDatasetName();
		}
		
		// add data as points and edges to model
		cartesianModel.addPlot(name, dataset);
		
		// commit canvas and model changes
		endUpdate();
		
		return name;
	}
	
	/**
	 * Add a new point to the graph.
	 * 
	 * @param point The point to add.
	 * 
	 * @param plotName The plot to add this point to. If {@code null}, the default plot will be used, or a new plot with
	 * a generated name if there are no plots available.
	 * 
	 * @return The {@link CartesianPoint} instance that was added.
	 */
	public CartesianPoint addPoint(Point2D point, String plotName) {
		if (plotName == null) {
			// get default plot name
			plotName = cartesianModel.getPlotDefault();
			
			if (plotName == null) {
				// get generated plot name
				plotName = generateDatasetName();
			}
		}
		
		CartesianPoint cpoint = cartesianModel.addPoint(plotName, point);
		
		// commit canvas and model changes
		endUpdate();
		
		return cpoint;
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
	 * Given placement of points on the canvas, update the viewport so all data graphics are visible.
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
}
