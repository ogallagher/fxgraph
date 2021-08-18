package com.fxgraph.cells;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 * A cartesian 2d plot point, with x,y coordinates, a text to show its coordinates, and a bullet (default is small circle).<br><br>
 * 
 * Since a cell implementation is inherently hierarchical, having parents and children, the "tree level" of a point is 
 * determined with its x coordinate, so that a parent always has a lesser x value than its child.<br><br>
 * 
 * Note also that a point is assumed to be a member of a valid function, in that for any x there is only one y. In other words,
 * {@code CartesianPoint} instances cannot be connected if they have the same x value.
 * 
 * @author <a href="https://github.com/ogallagher">ogallagher</a>
 * @since 17 August 2021
 */
public class CartesianPoint implements ICell, Comparable<CartesianPoint> {
	/**
	 * Default number formatting for a point label.
	 */
	private static NumberFormat defaultNumberFormat;
	
	/**
	 * A unique style class for targeting cartesian point graphics for css in the graph.
	 */
	public static final String UNIQUE_STYLE_CLASS = "cartesian-point";
	
	/**
	 * Default bullet radius.
	 */
	public static final double RADIUS_DEFAULT = 5;
	/**
	 * Default bullet type.
	 * 
	 * @see BulletType#DEFAULT
	 */
	public static final BulletType BULLET_TYPE_DEFAULT = BulletType.DEFAULT;
	/**
	 * Default fill color.
	 */
	public static final Color FILL_COLOR_DEFAULT = Color.BLACK;
	
	/**
	 * The <i>parent</i> point, of lesser x value.
	 */
	private CartesianPoint lesser;
	/**
	 * The <i>child</i> point, of greater x value.
	 */
	private CartesianPoint greater;
	
	/**
	 * The point's x value.
	 */
	private DoubleProperty x;
	/**
	 * The point's y value.
	 */
	private DoubleProperty y;
	
	/**
	 * The type of bullet used to represent this point in a graph canvas.
	 */
	private BulletType bulletType;
	
	/**
	 * The point's radius when displayed.
	 */
	private DoubleProperty radius;
	
	/**
	 * The point's fill color when displayed.
	 */
	private Property<Color> fillColor;
	
	/**
	 * The point's text that gets displayed as a label and contains coordinate information.
	 */
	private StringProperty text;
	
	static {
		defaultNumberFormat = NumberFormat.getNumberInstance();
		defaultNumberFormat.setMaximumFractionDigits(2);
		defaultNumberFormat.setRoundingMode(RoundingMode.HALF_UP);
	}
	
	/**
	 * Convenience constructor for {@link #CartesianPoint(double, double)}.
	 * 
	 * @param datapoint The point coordinates.
	 */
	public CartesianPoint(Point2D datapoint) {
		this(datapoint.getX(), datapoint.getY());
	}
	
	/**
	 * Convenience constructor for {@link #CartesianPoint(double, double, double, BulletType, Color)} with default
	 * values for other parameters.
	 * 
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 */
	public CartesianPoint(double x, double y) {
		this(x,y,RADIUS_DEFAULT,BULLET_TYPE_DEFAULT,FILL_COLOR_DEFAULT);
	}
	
	/**
	 * {@link CartesianPoint} constructor.
	 * 
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param r Bullet radius.
	 * @param bulletType
	 * @param fillColor
	 */
	public CartesianPoint(double x, double y, double r, BulletType bulletType, Color fillColor) {
		// set lesser and greater to null references
		lesser = null;
		greater = null;
		
		this.x = new SimpleDoubleProperty(x);
		this.y = new SimpleDoubleProperty(y);
		
		this.bulletType = bulletType;
		this.radius = new SimpleDoubleProperty(r);
		this.fillColor = new SimpleObjectProperty<Color>(fillColor);
		
		// bind text to point coordinates
		text = new SimpleStringProperty();
		final CartesianPoint self = this;
		ChangeListener<Number> updateText = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> v, Number ov, Number nv) {
				text.set(
					"(" + 
					defaultNumberFormat.format(self.x.get()) + 
					"," + 
					defaultNumberFormat.format(self.y.get()) +
					")"
				);
			}
		};
		updateText.changed(null, null, null);
		this.x.addListener(updateText);
		this.y.addListener(updateText);
	}
	
	/**
	 * @return A newly generated graphic to represent this point in the graph, including both the bullet
	 * and the label, translated so that the bullet is centered (layout position not affected).
	 */
	@Override
	public Region getGraphic(Graph graph) {
		final VBox graphic = new VBox();
		graphic.setPadding(Insets.EMPTY);
		graphic.setAlignment(Pos.CENTER);
		graphic.setFillWidth(false);
		
		Label label = new Label();
		Region bullet = new Region();
		
		// bind graphic location
		graphic.layoutXProperty().bindBidirectional(x);
		graphic.layoutYProperty().bindBidirectional(y);
		
		// translate to center bullet over point, assuming bullet is last child
		graphic.setTranslateX(0);
		graphic.translateYProperty().bind(
			radius.subtract(graphic.heightProperty().divide(2))
		);
		
		label.textProperty().bind(text);
		
		// add label to graphic
		graphic.getChildren().add(label);
		
		DoubleBinding dims = radius.multiply(2);
		bullet.prefWidthProperty().bind(dims);
		bullet.prefHeightProperty().bind(dims);
		
		// bind bullet fill color
		bullet.setBackground(new Background(new BackgroundFill(fillColor.getValue(), null, null)));
		
		fillColor.addListener((color, oldColor, newColor) -> {
			bullet.setBackground(new Background(new BackgroundFill(newColor, null, null)));
		});
		
		// create shape
		Shape bulletShape = null;
		
		switch (bulletType) {
			case CIRCLE:
			default:
				Circle circle = new Circle(1);
				bulletShape = circle;
		}
		
		if (bulletShape != null) {
			// shape will be scaled to fit region bounds
			bullet.setShape(bulletShape);
		}
		
		// add bullet to graphic
		graphic.getChildren().add(bullet);
		
		// add unique style class
		graphic.getStyleClass().add(UNIQUE_STYLE_CLASS);
		
		// center 
		
		return graphic;
	}
	
	/**
	 * A cartesian point has a single child with a strictly greater x value. If a closer child already exists,
	 * then the new cell will be a child of that closer child.
	 * 
	 * @param cell The child/descendant {@code CartesianPoint} cell.
	 * 
	 * @throws IllegalArgumentException If a child point with lesser or equal x value is attempted, or if {@code cell}
	 * is not a {@code CartesianPoint}.
	 */
	@Override
	public void addCellChild(ICell cell) throws IllegalArgumentException {
		if (cell instanceof CartesianPoint) {
			CartesianPoint point = (CartesianPoint) cell;
			
			if (point.compareTo(this) == -1) {
				throw new IllegalArgumentException(
					point + " must be strictly greater to be a child of " + this);
			}
			else if (greater != null) {
				int cmp = point.compareTo(greater);
				
				if (cmp == -1) {
					// child of this
					point.greater = greater;
					greater.lesser = point;
					
					greater = point;
					point.lesser = this;
				}
				else if (cmp == 1) {
					// descendant of greater, find position recursively
					greater.addCellChild(point);
				}
				else {
					throw new IllegalArgumentException(
						point + " must be strictly greater to be a child of " + greater);
				}
			}
			else {
				// set as greater
				greater = point;
				point.lesser = this;
			}
		}
		else {
			throw new IllegalArgumentException(
				"child cell " + cell + " must be a cartesian point to be a child to " + this);
		}
	}

	/**
	 * Returns a list of all <i>descendant</i> points, in ascending x value order (closest first).
	 */
	@Override
	public List<ICell> getCellChildren() {
		List<ICell> descendants = new LinkedList<>();
		CartesianPoint descendant = greater;
		
		while (descendant != null) {
			descendants.add(descendant);
			descendant = greater.greater;
		}
		
		return descendants;
	}
	
	/**
	 * Calls {@link #addCellChild(ICell) cell.addCellChild(this)} once a valid
	 * parent is found among descendants.
	 * 
	 * @throws IllegalArgumentException if {@code cell} is not a valid {@code CartesianPoint} parent.
	 */
	@Override
	public void addCellParent(ICell cell) throws IllegalArgumentException {
		if (cell instanceof CartesianPoint) {
			CartesianPoint parent = (CartesianPoint) cell;
			
			if (parent.compareTo(this) == -1) {
				parent.addCellChild(this);
			}
			else if (parent.lesser == null) {
				addCellChild(cell);
			}
			else {
				addCellParent(parent.lesser);
			}
		}
		else {
			throw new IllegalArgumentException(
				cell + " must be a cartesian point to be a parent of " + this);
		}
	}
	
	/**
	 * Returns a list of all <i>ancestor</i> points, in descending x value order (closest first).
	 */
	@Override
	public List<ICell> getCellParents() {
		List<ICell> ancestors = new LinkedList<>();
		CartesianPoint ancestor = lesser;
		
		while (ancestor != null) {
			ancestors.add(ancestor);
			ancestor = lesser.lesser;
		}
		
		return ancestors;
	}

	/**
	 * Removes the child point if it follows this point, even if transitively (is a descendant, and
	 * not technically a child). If {@code cell} is not a child or descendant of this point, nothing is done.<br><br>
	 * 
	 * Removing a child point does not remove that child's children; the sequence of descendant points is maintained
	 * apart from that child alone.
	 * 
	 * @throws IllegalArgumentException If {@code cell} is not a {@code CartesianPoint}.
	 */
	@Override
	public void removeCellChild(ICell cell) {
		if (cell instanceof CartesianPoint) {
			CartesianPoint point = (CartesianPoint) cell;
			
			if (greater == null) {
				// no child to remove, skip
			}
			else if (greater == point) {
				// remove child
				greater = point.greater;
			}
			else if (point.compareTo(greater) == 1) {
				// remove descendant
				greater.removeCellChild(cell);
			}
			else {
				// not a descendant, skip
			}
		}
		else {
			throw new IllegalArgumentException(
				cell + " must be a cartesian point to be a parent of " + this);
		}
	}
	
	/**
	 * Convenience method for {@link #removeCellChild(ICell) parent.removeCellChild(this)}.
	 */
	public void removeCellParent(ICell cell) {
		if (cell instanceof CartesianPoint) {
			cell.removeCellChild(this);
		}
		else {
			throw new IllegalArgumentException(
				cell + " must be a cartesian point to be a parent of " + this);
		}
	}
	
	/**
	 * Sorts points by x value, ascending.
	 */
	@Override
	public int compareTo(CartesianPoint other) {
		return Double.compare(x.get(), other.x.get());
	}
	
	/**
	 * @return x value property.
	 */
	public DoubleProperty xProperty() {
		return x;
	}
	
	/**
	 * @return y value property.
	 */
	public DoubleProperty yProperty() {
		return y;
	}
	
	/**
	 * @return radius property.
	 */
	public DoubleProperty radiusProperty() {
		return radius;
	}
	
	/**
	 * @return The point with the previous, lower x value, being the cell <i>parent</i>.
	 */
	public CartesianPoint getPrevious() {
		return lesser;
	}
	
	/**
	 * @return The point with the next, higher x value, being the cell <i>child</i>.
	 */
	public CartesianPoint getNext() {
		return greater;
	}
	
	/**
	 * A string with the class name, x,y coordinate values, and previous and next points.
	 */
	@Override
	public String toString() {
		String prefix, suffix;
		String self = "CartesianPoint(" + x.get() + "," + y.get() + ")";
		
		if (lesser == null) {
			prefix = "";
		}
		else {
			prefix = "(" + lesser.x.get() + "," + lesser.y.get() + ")-";
		}
		
		if (greater == null) {
			suffix = "";
		}
		else {
			suffix = "-(" + greater.x.get() + "," + greater.y.get() + ")";
		}
		
		return prefix + self + suffix;
	}
	
	/**
	 * Valid bullet types for representing a {@code CartesianPoint} in a graph canvas.
	 * 
	 * @author <a href="https://github.com/ogallagher">ogallagher</a>
	 * @since 17 August 2021
	 */
	public static enum BulletType {
		/**
		 * Represent a point with a circle. 
		 */
		CIRCLE;
		
		/**
		 * Default bullet type.
		 */
		private static final BulletType DEFAULT = CIRCLE;
	}
}
