package com.fxgraph.graph;

import com.fxgraph.cells.AbstractCell;
import com.fxgraph.edges.MessageEdge;
import com.fxgraph.layout.Layout;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A specialized graph that represents a 
 * <a href="https://en.wikipedia.org/wiki/Sequence_diagram">sequence diagram</a>.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 *
 */
public class SequenceDiagram extends Graph {
	/**
	 * Vertical spacing between message edges.
	 */
	private double verticalSpacing = 200;
	/**
	 * Horizontal spacing between actor life-lines.
	 */
	private double horizontalSpacing = 50;
	
	/**
	 * List of contained actor cells.
	 */
	private List<IActorCell> actors = new ArrayList<>();
	/**
	 * List of contained message edges.
	 */
	private List<IMessageEdge> messages = new ArrayList<>();
	
	/**
	 * Convenience method for {@link #addActor(IActorCell)}, defining an actor name and a life-line length.
	 * 
	 * @param actor Actor name
	 * @param length Life-line length.
	 */
	public void addActor(String actor, double length) {
		addActor(new ActorCell(actor, new SimpleDoubleProperty(length)));
	}
	
	/**
	 * Add an actor cell to the diagram.
	 * 
	 * @param actor The actor cell to add.
	 */
	public void addActor(IActorCell actor) {
		actors.add(actor);
		getModel().addCell(actor);
		repopulateCanvas();
	}

	/**
	 * Convenience method for {@link #addMessage(IMessageEdge)}, defining the actor cells and 
	 * edge message.
	 * 
	 * @param source Source actor cell.
	 * @param target Target actor cell.
	 * @param name Text label on the message edge.
	 */
	public void addMessage(IActorCell source, IActorCell target, String name) {
		addMessage(new MessageEdge(source, target, name));
	}
	
	/**
	 * Add a message edge to the diagram.
	 * 
	 * @param edge The message edge.
	 */
	public void addMessage(IMessageEdge edge) {
		messages.add(edge);
		getModel().addEdge(edge);
		repopulateCanvas();
	}
	
	/**
	 * Convenience method for {@link #layout(Layout) layout(null)}.
	 */
	public void layout() {
		layout(null);
	}
	
	/**
	 * Define a layout for placement of actor cells and message edges.
	 * 
	 * @param layout Optional {@link Layout} instance, usually {@code null}, so that a default 
	 * sequence diagram layout is used.
	 */
	@Override
	public void layout(Layout layout) {
		if (layout == null) {
			AtomicInteger counter = new AtomicInteger();
			actors.stream().map(actor -> getGraphic(actor)).forEach(actor -> {
				actor.setLayoutX(counter.getAndIncrement()*verticalSpacing);
				actor.setLayoutY(0);
				actor.toFront();
			});
			
			counter.set(0);
			messages.forEach(edge -> {
				edge.yOffsetProperty().set(counter.incrementAndGet() * horizontalSpacing);
			});
		}
	}
	
	/**
	 * @return Vertical spacing between messages.
	 */
	public double getVerticalSpacing() {
		return verticalSpacing;
	}

	/**
	 * @param verticalSpacing Vertical spacing between messages.
	 */
	public void setVerticalSpacing(double verticalSpacing) {
		this.verticalSpacing = verticalSpacing;
	}

	/**
	 * @return Horizontal spacing between actors.
	 */
	public double getHorizontalSpacing() {
		return horizontalSpacing;
	}

	/**
	 * @param horizontalSpacing Horizontal spacing between actors.
	 */
	public void setHorizontalSpacing(double horizontalSpacing) {
		this.horizontalSpacing = horizontalSpacing;
	}
	
	/**
	 * @return Actors list.
	 */
	public List<IActorCell> getActors() {
		return actors;
	}
	
	/**
	 * @return Message edge lists.
	 */
	public List<IMessageEdge> getMessages() {
		return messages;
	}

	/**
	 * Interface that any sequence diagram actor cell must implement. 
	 * 
	 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
	 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
	 *
	 */
	public interface IActorCell extends ICell {
		/**
		 * @return The actor's name.
		 */
		String getName();
	}

	/**
	 * An edge representing a sequence diagram message between actors.
	 * 
	 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
	 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
	 *
	 */
	public interface IMessageEdge extends IEdge {
		/**
		 * @return y-offset, defining the message's position in message history.
		 */
		DoubleProperty yOffsetProperty();
	}
	
	/**
	 * A specialized cell representing an actor in a sequence diagram.
	 * 
	 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
	 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
	 *
	 */
	public static class ActorCell extends AbstractCell implements IActorCell {
		/**
		 * The actor name.
		 */
		private final String name;
		/**
		 * Length of the actor's life-line, determining the start and end of its ability to message other actors.
		 */
		private final DoubleProperty lifeLineLength;
		
		/**
		 * Convenience constructor for {@link ActorCell#ActorCell(String, DoubleProperty)}.
		 * 
		 * @param name Actor name.
		 * @param lifeLineLength Life-line length.
		 */
		public ActorCell(String name, Double lifeLineLength) {
			this(name, new SimpleDoubleProperty(lifeLineLength));
		}
		
		/**
		 * {@link ActorCell} constructor.
		 * 
		 * @param name Actor name.
		 * @param lifeLineLength Life-line length property.
		 */
		public ActorCell(String name, DoubleProperty lifeLineLength) {
			this.name = name;
			this.lifeLineLength = lifeLineLength;
		}
		
		/**
		 * Create the graphics displayed in the graph canvas representing this actor, including the name and 
		 * the life-line.
		 */
		@Override
		public Region getGraphic(Graph graph) {
			Label label = new Label(name);
			Line lifeLine = new Line();
			lifeLine.getStyleClass().add("life-line");
			lifeLine.startXProperty().bind(label.widthProperty().divide(2));
			lifeLine.setStartY(0);
			lifeLine.endXProperty().bind(label.widthProperty().divide(2));
			lifeLine.endYProperty().bind(lifeLineLength);
			lifeLine.getStrokeDashArray().add(4d);
			Pane pane = new Pane(label, lifeLine);
			pane.getStyleClass().add("actor-cell");
			return pane;
		}
		
		/**
		 * Get the anchor point x-value, being the center of the actor's name label.
		 */
		@Override
		public DoubleBinding getXAnchor(Graph graph) {
			final Region graphic = graph.getGraphic(this);
			final Label label = (Label) graphic.getChildrenUnmodifiable().get(0);
			return graphic.layoutXProperty().add(label.widthProperty().divide(2));
		}
		
		/**
		 * Get the anchor point y-value, being the center of the actor's name label.
		 */
		@Override
		public DoubleBinding getYAnchor(Graph graph) {
			final Region graphic = graph.getGraphic(this);
			final Label label = (Label) graphic.getChildrenUnmodifiable().get(0);
			return graphic.layoutYProperty().add(label.heightProperty().divide(2));
		}
		
		public String getName() {
			return name;
		}
	}
}
