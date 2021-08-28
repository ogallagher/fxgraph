package com.fxgraph.graph;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.fxgraph.cells.CartesianPoint;
import com.fxgraph.cells.CartesianPoint.BulletType;
import com.fxgraph.edges.Edge;
import com.fxgraph.edges.SimpleEdge;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

/**
 * A model containing cells and edges in a manner specialized for storing multiple datasets/plots of 2d
 * coordinate points (cells) and their connections (edges).
 * 
 * @author <a href="https://github.com/ogallagher">ogallagher</a>
 * @since 17 August 2021
 * 
 */
public class MultiplotModel extends Model {
	private static final long serialVersionUID = 6156146377134782296L;
	
	/**
	 * Map of plots, each plot having a name for quick reference and a list of 
	 * {@link CartesianPoint CartesianPoints}.
	 */
	private HashMap<String,List<CartesianPoint>> plots;
	
	/**
	 * Default plot to use if a plot name is not specified. Set to reference the first plot created.
	 */
	private String plotDefault = null;
	
	/**
	 * {@link MultiplotModel} constructor.
	 */
	public MultiplotModel() {
		super();
	}
	
	/**
	 * In addition to clearing general model cell and edge references, clear {@link #plots} as well.
	 */
	@Override
	public void clear() {
		super.clear();
		
		this.plots = new HashMap<>();
		plotDefault = null;
	}
	
	/**
	 * @return Number of plots.
	 */
	public int getPlotsCount() {
		return plots.size();
	}
	
	/**
	 * @return The default plot name, or {@code null} if no plots exist.
	 */
	public String getPlotDefault() {
		return plotDefault;
	}
	
	/**
	 * Set the default plot name.
	 * 
	 * @param plotName Name string referencing the default plot.
	 * 
	 * @throws IllegalArgumentException If {@code plotName} is not yet in this model. 
	 */
	public void setPlotDefault(String plotName) throws IllegalArgumentException {
		plotDefault = plotName;
	}
	
	/**
	 * Convenience method for {@link #addPlot(String, Collection, double, BulletType, Color)} with default point styling.
	 * @param name
	 * @param dataset
	 */
	public void addPlot(String name, Collection<Point2D> dataset) {
		addPlot(name, dataset, CartesianPoint.RADIUS_DEFAULT, CartesianPoint.BULLET_TYPE_DEFAULT, CartesianPoint.FILL_COLOR_DEFAULT);
	}
	
	/**
	 * Convert a dataset of raw points to {@link CartesianPoint} plots with given styling and add to this graph. If there is
	 * already a plot of the given name, the previous plot is replaced with the new one.
	 * 
	 * @param name Plot name.
	 * @param dataset Plot points.
	 * @param pointRadius Radius for each point bullet.
	 * @param pointBullet Point bullet type.
	 * @param pointFill Point bullet fill color.
	 */
	public void addPlot(String name, Collection<Point2D> dataset, double pointRadius, BulletType pointBullet, Color pointFill) {
		if (plotDefault == null) {
			plotDefault = name;
		}
		
		// add points to plot
		List<CartesianPoint> plot = new LinkedList<>();
		CartesianPoint previous = null;
		
		for (Point2D point : dataset) {
			// create new point
			CartesianPoint current = new CartesianPoint(point.getX(), point.getY(), pointRadius, pointBullet, pointFill);
			
			// connect adjacent points
			if (previous != null) {
				current.addCellParent(previous);
			}
			
			// add point to newly added cells list
			super.addCell(current);
			
			// add point to plot
			plot.add(current);
			
			previous = current;
		}
		
		// sort points by x value
		plot.sort(Comparator.naturalOrder());
		
		// create edges between consecutive points
		for (CartesianPoint point : plot) {
			// create edge between consecutive points
			addEdge(point);
		}
		
		// add new plot to plots
		this.plots.put(name, plot);
	}
	
	// TODO MultiplotModel.removePlot
	
	/**
	 * Add a point to the given plot with styling. If the plot name doesn't yet exist, create a new plot.<br><br>
	 * 
	 * Edges to connect new points in the plot are not created, and must be added explicitly.
	 * 
	 * @param plotName Name of the plot to add the point to. 
	 * @param point The coordinates of the new point.
	 * 
	 * @return A reference to the added point.
	 * 
	 * @throws IllegalArgumentException If there is already a point in the given plot with the same x value.
	 */
	public CartesianPoint addPoint(
			String plotName, Point2D point, double pointRadius, BulletType pointBullet, Color pointFill) 
			throws IllegalArgumentException {
		// create graph point
		CartesianPoint cpoint = new CartesianPoint(point.getX(), point.getY(), pointRadius, pointBullet, pointFill);
		
		// find plot
		List<CartesianPoint> plot = plots.get(plotName);
		if (plot == null) {
			// new plot
			plot = new LinkedList<>();
			plots.put(plotName, plot);
			
			if (plotDefault == null) {
				plotDefault = plotName;
			}
		}
		
		if (plot.isEmpty()) {
			// place at beginning
			plot.add(cpoint);
		}
		else {
			// place in plot according to x value
			CartesianPoint first = plot.get(0);
			int cmp = cpoint.compareTo(first);
			
			if (cmp == 1) {
				first.addCellChild(cpoint);
				
				if (cpoint.getNext() == null) {
					// place at end
					plot.add(cpoint);
				}
				else {
					// place before next
					plot.add(plot.indexOf(cpoint.getNext()), cpoint);
				}
			}
			else {
				// assume -1, but could throw exception of 0
				cpoint.addCellChild(first);
				
				// place at beginning
				plot.add(0,cpoint);
			}
		}
		
		// add point to newly added cells
		super.addCell(cpoint);
		
		return cpoint;
	}
	
	/**
	 * Convenience method for {@link #addPoint(String, Point2D, double, BulletType, Color)} with default styling.
	 * 
	 * @param plotName Name of destination plot.
	 * @param point Point to add.
	 * 
	 * @return Added {@code CartesianPoint} instance.
	 */
	CartesianPoint addPoint(String plotName, Point2D point) {
		return addPoint(plotName, point, CartesianPoint.RADIUS_DEFAULT, CartesianPoint.BULLET_TYPE_DEFAULT, CartesianPoint.FILL_COLOR_DEFAULT);
	}
	
	/**
	 * Use {@link #addPoint(String, Point2D, boolean)} instead.
	 * 
	 * @throws UnsupportedOperationException Not allowed to attempt adding a cell that is not a point in a plot.
	 */
	@Override
	public void addCell(ICell cell) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("not allowed to add a cell without it being a point in a plot");
	}
	
	/**
	 * Remove a cell from the model. If the cell is not found in the model, this method does nothing.
	 * 
	 * @param cell The cell to remove.
	 */
	@Override
	public void removeCell(ICell cell) {
		if (cell != null && cell instanceof CartesianPoint) {
			// remove from plot
			for (Collection<CartesianPoint> plot : plots.values()) {
				if (plot.remove(cell)) {
					// point found
					break;
				}
			}
			
			// add cell to newly removed cells
			super.removeCell(cell);
		}
		// else, skip
	}
	
	/**
	 * Create a {@code SimpleEdge} between the source point and the next point in its plot. If the source
	 * point is the last one, then no edge is added.<br><br>
	 * 
	 * The source point must already be linked to the target to determine the next point with
	 * {@link CartesianPoint#getNext()}.
	 * 
	 * @param sourcePoint The point from which the edge starts.
	 * 
	 * @return The added edge, or {@code null} if {@code sourcePoint} is the last in its plot.
	 */
	public SimpleEdge addEdge(CartesianPoint sourcePoint) {
		CartesianPoint targetPoint = sourcePoint.getNext();
		
		if (targetPoint != null) {
			SimpleEdge edge = new SimpleEdge(sourcePoint, targetPoint, false);
			
			// add edge to newly added edges
			super.addEdge(edge);
			
			return edge;
		}
		else {
			// skip edge from terminal point
			return null;
		}
	}
	
	/**
	 * Create a {@code SimpleEdge} between the second-to-last and last points in the given plot.
	 * 
	 * @param plotName The plot to which to add the edge. If {@code null}, the {@link #getPlotDefault() default plot} 
	 * is used.
	 * 
	 * @return The added edge, or {@code null} if the plot was not found or the plot didn't have at least 2 points.
	 */
	public SimpleEdge addLastEdge(String plotName) {
		if (plotName == null) {
			plotName = this.plotDefault;
		}
		
		List<CartesianPoint> plot = plots.get(plotName);
		
		if (plot == null || plot.size() < 2) {
			return null;
		}
		else {
			return addEdge(plot.get(plot.size()-2));
		}
	}
	
	/**
	 * Convenience method for {@link #addEdge(IEdge)}, adding a {@link SimpleEdge} instance between two
	 * {@code CartesianPoint} cells.
	 * 
	 * @deprecated This method is slower and more prone to error than {@link #addEdge(CartesianPoint)}.
	 * 
	 * @param sourceCell The edge source cell.
	 * @param targetCell The edge target cell.
	 * 
	 * @see #addEdge(IEdge)
	 */
	@Override
	public void addEdge(ICell sourceCell, ICell targetCell) {
		addEdge(new SimpleEdge(sourceCell, targetCell, false));
	}
	
	/**
	 * Adds an edge to the model, ensuring that it is not {@code null}, and that it connects 
	 * two adjacent {@code CartesianPoint} cells that belong to the same plot.<br><br>
	 * 
	 * Ultimately calls {@link #addEdge(CartesianPoint)} once method parameters have been validated.
	 * 
	 * @deprecated This method is slower and more prone to error than {@link #addEdge(CartesianPoint)}.
	 * 
	 * @throws NullPointerException If {@code edge == null}.
	 * 
	 * @throws IllegalArgumentException If the source or target cell is not a {@code CartesianPoint}, or if they are
	 * not valid sequential points.
	 */
	@Override
	public void addEdge(IEdge edge) throws NullPointerException, IllegalArgumentException {
		if (edge == null) {
			throw new NullPointerException("Cannot add a null edge");
		}
		else {
			ICell sourceCell = edge.getSource();
			ICell targetCell = edge.getTarget();
			
			if (sourceCell instanceof CartesianPoint && targetCell instanceof CartesianPoint) {
				CartesianPoint sourcePoint = (CartesianPoint) sourceCell;
				
				if (sourcePoint.getNext() == targetCell) {
					addEdge(sourcePoint);
				}
				else {
					throw new IllegalArgumentException(
						"a multiplot model can only create edges between sequential points within the same plot");
				}
			}
			else {
				throw new IllegalArgumentException(
					sourceCell + " and " + targetCell + " must be cartesian points to be connected in a multiplot model");
			}
		}
	}
	
	/**
	 * Prevents default model behavior to allow orphan cells (cells that have no parent). The first point in a plot has
	 * no parent, and the last point has no child.
	 */
	@Override
	public void attachOrphansToGraphParent(List<ICell> cellList) {}
}
