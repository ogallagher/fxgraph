package com.fxgraph.layout;

import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import org.abego.treelayout.Configuration;
import org.abego.treelayout.Configuration.Location;
import org.abego.treelayout.NodeExtentProvider;
import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.util.DefaultConfiguration;
import org.abego.treelayout.util.DefaultTreeForTreeLayout;

/**
 * Places in a compact manner the hierarchical tree of nodes in a graph according to the 
 * <a href="http://treelayout.sourceforge.net">abego tree layout algorithm</a>.
 * 
 * @author <a href="https://github.com/sirolf2009">sirolf2009</a>
 * @author <a href="https://github.com/ogallagher">ogallagher</a> (javadoc)
 *
 */
public class AbegoTreeLayout implements Layout {
	/**
	 * Configuration for the abego tree layout (spacing, tree orientation and root placement).
	 */
	private final Configuration<ICell> configuration;

	/**
	 * Configure an abego tree layout with {@code 100} vertical gap between levels,
	 * {@code 45} horizontal gap between nodes, and a tree with the root at the
	 * {@link Location#Top Top}.
	 */
	public AbegoTreeLayout() {
		this(100, 45, Location.Top);
	}
	
	/**
	 * Convenience constructor for defining abego tree layout {@link Configuration configuration} params.
	 * 
	 * @param gapBetweenLevels Vertical gap between hierarchical levels.
	 * @param gapBetweenNodes Horizontal gap between nodes.
	 * @param location Where to place the root node (ex. top grow down, left grow right, etc).
	 */
	public AbegoTreeLayout(double gapBetweenLevels, double gapBetweenNodes, Location location) {
		this(new DefaultConfiguration<ICell>(gapBetweenLevels, gapBetweenNodes, location));
	}

	/**
	 * Fully configure the abego tree layout.
	 * 
	 * @param configuration abego tree layout configuration object.
	 */
	public AbegoTreeLayout(Configuration<ICell> configuration) {
		this.configuration = configuration;
	}

	@Override
	public void execute(Graph graph) {
		final DefaultTreeForTreeLayout<ICell> layout = new DefaultTreeForTreeLayout<>(graph.getModel().getRoot());
		addRecursively(layout, graph.getModel().getRoot());
		final NodeExtentProvider<ICell> nodeExtentProvider = new NodeExtentProvider<ICell>() {
			@Override
			public double getWidth(ICell tn) {
				if(tn == graph.getModel().getRoot()) {
					return 0;
				}
				return graph.getGraphic(tn).getWidth();
			}

			@Override
			public double getHeight(ICell tn) {
				if(tn == graph.getModel().getRoot()) {
					return 0;
				}
				return graph.getGraphic(tn).getHeight();
			}
		};
		final TreeLayout<ICell> treeLayout = new TreeLayout<>(layout, nodeExtentProvider, configuration);
		treeLayout.getNodeBounds().entrySet().stream().filter(entry -> entry.getKey() != graph.getModel().getRoot()).forEach(entry -> {
			graph.getGraphic(entry.getKey()).setLayoutX(entry.getValue().getX());
			graph.getGraphic(entry.getKey()).setLayoutY(entry.getValue().getY());
		});
	}

	/**
	 * Recursively traverse the graph cell hierarchy and add each cell to the tree layout.
	 *  
	 * @param layout
	 * @param node
	 */
	public void addRecursively(DefaultTreeForTreeLayout<ICell> layout, ICell node) {
		node.getCellChildren().forEach(cell -> {
			if(!layout.hasNode(cell)) {
				layout.addChild(node, cell);
				addRecursively(layout, cell);
			}
		});
	}

}
