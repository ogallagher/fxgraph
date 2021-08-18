package com.fxgraph.test;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.fxgraph.cells.CartesianPoint;
import com.fxgraph.graph.CartesianGraph;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Demonstration of a {@link CartesianGraph cartesian graph} with multiple datasets/plots over a set of
 * axes.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a>
 * 
 */
public class CartesianGraphDemo extends Application {
	/**
	 * Program entrypoint, called by javafx on gui launch.
	 */
	@Override
	public void start(Stage stage) throws Exception {
		CartesianGraph graph = new CartesianGraph(CartesianGraph.PlotMode.CONNECTED_POINTS);
		
		graph.getUseViewportGestures().set(true);
		graph.getUseNodeGestures().set(true);
		
		// add example datasets to the graph
		populateGraph(graph);
		
		// configure initial viewport
		graph.layout();
		
		// Configure interaction buttons and behavior
		graph.getViewportGestures().setPanButton(MouseButton.PRIMARY);
		graph.getNodeGestures().setDragButton(MouseButton.PRIMARY);
		
		// Display the graph
		stage.setScene(new Scene(new BorderPane(graph.getCanvas())));
		stage.show();
	}
	
	private static void populateGraph(CartesianGraph graph) {
		float domain = 500;
		float halfRange = 250;
		double numPi = Math.PI * 1.5;
		
		// 1. add a full named dataset, in scrambled order
		List<Point2D> dataset = new LinkedList<Point2D>();
		for (double t=0; t<1.0; t+=0.05) {
			dataset.add(new Point2D(t * domain, Math.sin(t * numPi) * halfRange + halfRange));
		}
		
		// randomize point order (should be fixed by graph)
		dataset.sort(new Comparator<Point2D>() {
			@Override
			public int compare(Point2D p1, Point2D p2) {
				return (int) Math.round(Math.random()*2 - 1);
			}
		});
		
		// add to graph
		System.out.println("adding dataset 1 with " + dataset.size() + " points to graph");
		graph.addDataset(dataset, "red wave", 5, CartesianPoint.BulletType.CIRCLE, Color.RED);
		
		// 2. add a dataset using individual points
		List<CartesianPoint> cpoints = new LinkedList<>();
		for (double t=0; t<1.0; t+=0.04) {
			cpoints.add(
				graph.addPoint(
					new Point2D(t * domain, Math.cos(t * numPi) * halfRange + halfRange), 
					"purple wave",
					5,
					CartesianPoint.BulletType.CIRCLE,
					Color.BLUEVIOLET
				)
			);
		}
		
		for (CartesianPoint cpoint : cpoints) {
			graph.addEdge(cpoint);
		}
	}
}
