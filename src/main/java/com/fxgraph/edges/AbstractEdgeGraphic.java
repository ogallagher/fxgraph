package com.fxgraph.edges;

import com.fxgraph.graph.Arrow;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An abstract edge graphic superclass with a group for basic edge graphics, a text label, and an arrow
 * for showing a directed line.
 * <br><br>
 * Includes methods for calculating the terminal point for an edge given the target graphic and a
 * fallback target point.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 *
 */
public abstract class AbstractEdgeGraphic extends Pane {
	/**
	 * The group of graphics core to representing the edge in the graph canvas, empty by default.
	 */
	protected final Group group = new Group();
	/**
	 * The edge text, empty by default.
	 */
	protected final Text text = new Text();
	/**
	 * The edge arrow to show direction, empty by default.
	 */
	protected final Arrow arrow = new Arrow();

	/**
	 * @return Principal edge graphics.
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * @return Edge text label.
	 */
	public Text getText() {
		return text;
	}

	/**
	 * @return Edge arrow.
	 */
	public Arrow getArrow() {
		return arrow;
	}

	/**
	 * Binds the arrow start and end points to given point components, and adds a style class to the arrow.
	 * 
	 * @param sourceX Observable start x.
	 * @param sourceY Observable start y.
	 * @param targetX Observable end x.
	 * @param targetY Observable end y.
	 */
	protected void setupArrow(DoubleBinding sourceX, DoubleBinding sourceY, DoubleBinding targetX, DoubleBinding targetY) {
		// Set position bindings
		arrow.startXProperty().bind(sourceX);
		arrow.startYProperty().bind(sourceY);
		arrow.endXProperty().bind(targetX);
		arrow.endYProperty().bind(targetY);
		// Add style
		arrow.getStyleClass().add("arrow");
	}
	
	/**
	 * Given a target graphic to connect with, attempt to find an edge target point to which to connect the arrow.
	 * On failure to find an edge target point, the raw target location is used.
	 * <br><br>
	 * Ultimately calls {@link #setupArrow(DoubleBinding, DoubleBinding, DoubleBinding, DoubleBinding)} using the modified 
	 * target point.
	 * 
	 * @param target Target graphic to connect with (typically a cell graphic).
	 * @param sourceX Observable start x.
	 * @param sourceY Observable start y.
	 * @param targetX Observable end x.
	 * @param targetY Observable end y.
	 */
	protected void setupArrowIntersect(Region target, DoubleBinding sourceX, DoubleBinding sourceY, DoubleBinding targetX, DoubleBinding targetY) {
		// Fallback point, should not be necessary for normal shapes
		final Point2D fallback = new Point2D(targetX.get(), targetY.get());
		
		// Calculate where the arrow should be drawn based on the intersection with the shape.
		final DoubleBinding targetXFitted = Bindings.createDoubleBinding(() -> getIntercept(
				new Point2D(sourceX.get(), sourceY.get()),
				new Point2D(targetX.get(), targetY.get()),
				target).orElse(fallback).getX(),
				// dependency properties
				targetX, targetY, sourceX, sourceY);
		
		final DoubleBinding targetYFitted = Bindings.createDoubleBinding(() -> getIntercept(
				new Point2D(sourceX.get(), sourceY.get()),
				new Point2D(targetX.get(), targetY.get()),
				target).orElse(fallback).getY(),
				// dependency properties
				targetX, targetY, sourceX, sourceY);
		
		// Set position bindings and style
		setupArrow(sourceX, sourceY, targetXFitted, targetYFitted);
	}

	/**
	 * Given a line from {@code source} to {@code target}, find an appropriate section with the edge of the target
	 * graphic. Currently only works accurately for rectangular targets, using the bounding box.
	 * 
	 * TODO extend to accurately terminate on the edge of any target shape.
	 * 
	 * @param source Source point.
	 * @param target Target point.
	 * @param shape Target shape.
	 * @return Optional target edge point.
	 */
	protected Optional<Point2D> getIntercept(Point2D source, Point2D target, Region shape) {
		// Create the lines that represent the shape (Currently rectangle-only, also see below for rectangle-assumed logic)
		Point2D shapeNW = new Point2D(shape.getLayoutX(), shape.getLayoutY());
		Point2D shapeNE = new Point2D(shape.getLayoutX() + shape.getWidth(), shape.getLayoutY());
		Point2D shapeSW = new Point2D(shape.getLayoutX(), shape.getLayoutY() + shape.getHeight());
		Point2D shapeSE = new Point2D(shape.getLayoutX() + shape.getWidth(), shape.getLayoutY() + shape.getHeight());
		Point2D[][] shapeLines = new Point2D[][] {
				{ shapeNW, shapeNE },
				{ shapeNE, shapeSE },
				{ shapeSE, shapeSW },
				{ shapeSW, shapeNW },
		};

		// Collect the intersections with the shape and sort based on distance from the source.
		SortedSet<Point2D> intersections = new TreeSet<>((p1, p2) -> {
			double p1Dist= p1.distance(source);
			double p2Dist= p2.distance(source);
			if (p1Dist > p2Dist) {
				return 1;
			} else if (p1Dist == p2Dist) {
				return 0;
			} else {
				return -1;
			}
		});

		// For each line creating the shape check its intersection points.
		// Discard any intersection that is not within the shape's bounds (assumed rectangle).
		for (Point2D[] shapeLine : shapeLines) {
			Point2D intersect = calculateInterceptionPoint(source, target, shapeLine[0], shapeLine[1]);
			if (intersect != null &&
					intersect.getX() >= shape.getLayoutX() &&
					intersect.getX() <= shape.getLayoutX() + shape.getWidth() &&
					intersect.getY() >= shape.getLayoutY() &&
					intersect.getY() <= shape.getLayoutY() + shape.getHeight()) {
				intersections.add(intersect);
			}
		}
		if (intersections.isEmpty()) {
			return Optional.empty();
		}
		
		return Optional.of(intersections.first());
	}

	/**
	 * Given endpoints for two straight segments, calculate an intersection point.
	 * 
	 * @param line1Start First line start point.
	 * @param line1End First line end point.
	 * @param line2Start Second line start point.
	 * @param line2End Second line end point.
	 * 
	 * @return The intersection point, or {@code null} if none exist.
	 */
	private static Point2D calculateInterceptionPoint(Point2D line1Start, Point2D line1End,
													  Point2D line2Start, Point2D line2End) {
		double xDiff1 = line1Start.getX() - line1End.getX();
		double yDiff1 = line1End.getY() - line1Start.getY();
		double mod1 =
				yDiff1 * line1Start.getX() +
				xDiff1 * line1Start.getY();

		double xDiff2 = line2Start.getX() - line2End.getX();
		double yDiff2 = line2End.getY() - line2Start.getY();
		double mod2 =
				yDiff2 * line2Start.getX() +
				xDiff2 * line2Start.getY();

		double delta = yDiff1 * xDiff2 - yDiff2 * xDiff1;
		if (delta == 0) {
			return null;
		}
		return new Point2D(
				((xDiff2 * mod1 - xDiff1 * mod2) / delta),
				((yDiff1 * mod2 - yDiff2 * mod1) / delta));
	}
}
