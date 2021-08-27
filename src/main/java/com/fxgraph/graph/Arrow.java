package com.fxgraph.graph;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.shape.Line;

/**
 * A straight arrow, composed of a main line and a fin line connected to either side of the end point.
 * 
 * @see The <a href="https://stackoverflow.com/a/41353991/10200417">stackoverflow answer</a> 
 * that this class is derived from.
 */
public class Arrow extends Group {
	/**
	 * The main central line, to either side of which the arrow's two fins are connected.
	 */
    private final Line line;
    
    /**
     * Convenience constructor for {@link #Arrow(Line, Line, Line)} with three empty lines.
     */
    public Arrow() {
        this(new Line(), new Line(), new Line());
    }
    
    /**
     * Convenience constructor for {@link #Arrow(Line, Line, Line, double, double)}, with default arrow length and arrow width.
     * 
     * @param line Central main line.
     * @param arrow1 First fin line. 
     * @param arrow2 Second fin line.
     */
    public Arrow(Line line, Line arrow1, Line arrow2) {
    	this(line, arrow1, arrow2, 10, 5);
    }
    
    /**
     * @param line Central main line.
     * @param arrow1 First fin line, whose end points will be updated depending on the arrow dimensions 
     * and the direction of the main line.
     * @param arrow2 Second fin line, similar to the first.
     * @param arrowLength The length of each arrow line.
     * @param arrowWidth The width of the arrow (distance between the main line and a fin line).
     */
    public Arrow(Line line, Line arrow1, Line arrow2, double arrowLength, double arrowWidth) {
        super(line, arrow1, arrow2);
        
        this.line = line;
        InvalidationListener updater = o -> {
            double ex = getEndX();
            double ey = getEndY();
            double sx = getStartX();
            double sy = getStartY();
            
            arrow1.setEndX(ex);
            arrow1.setEndY(ey);
            arrow2.setEndX(ex);
            arrow2.setEndY(ey);
            
            if (ex == sx && ey == sy) {
                // arrow parts of length 0
                arrow1.setStartX(ex);
                arrow1.setStartY(ey);
                arrow2.setStartX(ex);
                arrow2.setStartY(ey);
            }
            else {
                double factor = arrowLength / Math.hypot(sx-ex, sy-ey);
                double factorO = arrowWidth / Math.hypot(sx-ex, sy-ey);
                
                // part in direction of main line
                double dx = (sx - ex) * factor;
                double dy = (sy - ey) * factor;
                
                // part orthogonal to main line
                double ox = (sx - ex) * factorO;
                double oy = (sy - ey) * factorO;
                
                arrow1.setStartX(ex + dx - oy);
                arrow1.setStartY(ey + dy + ox);
                arrow2.setStartX(ex + dx + oy);
                arrow2.setStartY(ey + dy - ox);
            }
        };
        
        // add updater to properties
        startXProperty().addListener(updater);
        startYProperty().addListener(updater);
        endXProperty().addListener(updater);
        endYProperty().addListener(updater);
        updater.invalidated(null);
    }

    // start/end properties

    /**
     * @param value Main line start x.
     */
    public final void setStartX(double value) {
        line.setStartX(value);
    }

    /**
     * @return Main line start x.
     */
    public final double getStartX() {
        return line.getStartX();
    }

    /**
     * @return Main line start x property.
     */
    public final DoubleProperty startXProperty() {
        return line.startXProperty();
    }
    
    /**
     * @param value Main line start y.
     */
    public final void setStartY(double value) {
        line.setStartY(value);
    }
    
    /**
     * @return Main line start y.
     */
    public final double getStartY() {
        return line.getStartY();
    }
    
    /**
     * @return Main line start y property.
     */
    public final DoubleProperty startYProperty() {
        return line.startYProperty();
    }

    /**
     * @param value Main line end x.
     */
    public final void setEndX(double value) {
        line.setEndX(value);
    }

    /**
     * @return Main line end x.
     */
    public final double getEndX() {
        return line.getEndX();
    }

    /**
     * @return Main line end x property.
     */
    public final DoubleProperty endXProperty() {
        return line.endXProperty();
    }

    /**
     * @param value Main line end y.
     */
    public final void setEndY(double value) {
        line.setEndY(value);
    }
    
    /**
     * @return Main line end y.
     */
    public final double getEndY() {
        return line.getEndY();
    }

    /**
     * @return Main line end y property.
     */
    public final DoubleProperty endYProperty() {
        return line.endYProperty();
    }

}