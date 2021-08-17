package com.fxgraph.cells;

import com.fxgraph.graph.Graph;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A resizable cell represented by a blue rectangle, of size 50.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 *
 */
public class RectangleCell extends AbstractCell {
	/**
	 * Empty default constructor.
	 */
	public RectangleCell() {
	}
	
	/**
	 * Create the resizable blue rectangle graphic to represent this cell.
	 * 
	 * @see CellGestures#makeResizable(Graph, Region)
	 */
	@Override
	public Region getGraphic(Graph graph) {
		final Rectangle view = new Rectangle(50, 50);

		view.setStroke(Color.DODGERBLUE);
		view.setFill(Color.DODGERBLUE);

		final Pane pane = new Pane(view);
		pane.setPrefSize(50, 50);
		view.widthProperty().bind(pane.prefWidthProperty());
		view.heightProperty().bind(pane.prefHeightProperty());
		CellGestures.makeResizable(graph, pane);

		return pane;
	}

}
