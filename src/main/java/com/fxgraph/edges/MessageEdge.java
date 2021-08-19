package com.fxgraph.edges;

import com.fxgraph.graph.Graph;
import com.fxgraph.graph.SequenceDiagram.IActorCell;
import com.fxgraph.graph.SequenceDiagram.IMessageEdge;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * A fully defined {@code AbstractEdge} implementation specialized for representing sequence diagram messages.
 * <br><br>
 * A message edge has a name and a y offset, and is always represented as a directed horizontal line between
 * actor life-lines.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 * 
 * @see com.fxgraph.graph.SequenceDiagram
 * @see com.fxgraph.graph.SequenceDiagram.IMessageEdge
 *
 */
public class MessageEdge extends AbstractEdge implements IMessageEdge {
	/**
	 * Message name.
	 */
	private final String name;
	/**
	 * Message y-offset, defining its place in the sequence diagram's message history. 
	 */
	private final DoubleProperty yOffsetProperty = new SimpleDoubleProperty();
	
	/**
	 * {@link MessageEdge} constructor.
	 * 
	 * @param source Source actor, which sends the message.
	 * @param target Target actor, which receives the message.
	 * @param name Message name.
	 */
	public MessageEdge(IActorCell source, IActorCell target, String name) {
		super(source, target, true);
		this.name = name;
	}

	@Override
	public DoubleProperty yOffsetProperty() {
		return yOffsetProperty;
	}

	@Override
	public Region getGraphic(Graph graph) {
		return new EdgeGraphic(graph, this);
	}
	
	/**
	 * TODO Respond to a mouse entering this edge's associated graphic. This will not work properly until
	 * {@code MessageEdge.EdgeGraphic} is fixed to only be as large as it needs to be.
	 * 
	 * @see <a href="https://github.com/ogallagher/fxgraph/issues/13#issuecomment-901334463">ogallagher/fxgraph #13</a>
	 */
	@Override
	public boolean onHoverBegin(Graph graph, Region graphic) {
		// System.out.println(this.name + " hover begin");
		return false;
	}
	
	/**
	 * TODO Respond to a mouse leaving this edge's associated graphic.
	 */
	@Override
	public boolean onHoverEnd(Graph graph, Region graphic) {
		// System.out.println(this.name + " hover end");
		return false;
	}

	/**
	 * An {@code AbstractEdgeGraphic} implementation specialized for a message edge, having a label for the
	 * message name, and a horizontal arrow between actors at a specified y-offset.
	 * 
	 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
	 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
	 *
	 */
	private static class EdgeGraphic extends AbstractEdgeGraphic {
		/**
		 * {@link EdgeGraphic} constructor.
		 * 
		 * @param graph The graphic to whose canvas this graphic will be added.
		 * @param edge The message edge represented by this edge graphic.
		 */
		public EdgeGraphic(Graph graph, MessageEdge edge) {
			final DoubleBinding sourceX = edge.getSource().getXAnchor(graph);
			final DoubleBinding sourceY = edge.getSource().getYAnchor(graph).add(edge.yOffsetProperty);
			final DoubleBinding targetX = edge.getTarget().getXAnchor(graph);
			final DoubleBinding targetY = edge.getTarget().getYAnchor(graph).add(edge.yOffsetProperty);
			
			// arrow style class
			arrow.getStyleClass().add("arrow");
			
			// arrow endpoint bindings
			arrow.startXProperty().bind(sourceX);
			arrow.startYProperty().bind(sourceY);
			
			arrow.endXProperty().bind(targetX);
			arrow.endYProperty().bind(targetY);
			
			// add arrow to edge graphic group
			group.getChildren().add(arrow);

			final DoubleProperty textWidth = new SimpleDoubleProperty();
			final DoubleProperty textHeight = new SimpleDoubleProperty();
			
			// TODO [ogallagher] AbstractEdgeGraphic already defines a text member; see ogallagher/fxgraph/issues/11
			Text text = new Text(edge.name);
			// style class for 
			text.getStyleClass().add("edge-text");
			text.xProperty().bind(sourceX.add(targetX).divide(2).subtract(textWidth.divide(2)));
			text.yProperty().bind(sourceY.add(targetY).divide(2).subtract(textHeight.divide(2)));
			final Runnable recalculateWidth = () -> {
				textWidth.set(text.getLayoutBounds().getWidth());
				textHeight.set(text.getLayoutBounds().getHeight());
			};
			text.parentProperty().addListener((obs, oldVal, newVal) -> recalculateWidth.run());
			text.textProperty().addListener((obs, oldVal, newVal) -> recalculateWidth.run());
			group.getChildren().add(text);
			
			// add edge graphic group to pane child widgets
			getChildren().add(group);
			
			// add style class to edge graphic
			getStyleClass().add("message-edge");
			
			// see https://github.com/ogallagher/fxgraph/issues/13#issuecomment-901334463
			// setBackground(new Background(new BackgroundFill(new Color(0,0,0,0.1), null, null)));
		}
	}
}
