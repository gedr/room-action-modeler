package ru.intech.ussd.modeler.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.util.Map;

import javax.swing.JPanel;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.TransformerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.intech.ussd.modeler.config.GraphConfig;
import ru.intech.ussd.modeler.control.CreateEdgeControlImpl;
import ru.intech.ussd.modeler.control.RoomEditingModalGraphMouse;
import ru.intech.ussd.modeler.entities.Room;
import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.graphobjects.VertexRoom;
import ru.intech.ussd.modeler.graphobjects.VertexSpecial;
import ru.intech.ussd.modeler.transformers.GraphToFlatTranformer;
import ru.intech.ussd.modeler.transformers.RoomShapeTransformer;
import ru.intech.ussd.modeler.transformers.RoomVertexIconTranformer;
import ru.intech.ussd.modeler.util.Unit;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class GraphPanel extends JPanel {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(GraphPanel.class);

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private Graph<Vertex, Unit<Edge>> graph;
	private Map<Vertex, Point2D> map;
	private VisualizationModel<Vertex, Unit<Edge>> vm;
	private VisualizationViewer<Vertex, Unit<Edge>> vv;
	private ScalingControl scaler;
	private RoomShapeTransformer roomShapeTransformer;
	private RoomVertexIconTranformer roomVertexIconTranformer;

	private GraphConfig config;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public GraphPanel(Graph<Vertex, Unit<Edge>> graph, GraphConfig config) {
		this.graph = graph;
		this.setConfig(config);
		setLayout(new BorderLayout());
		init();
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public GraphConfig getConfig() {
		if (config == null) {
			config = new GraphConfig();
		}
		return config;
	}

	public void setConfig(GraphConfig config) {
		this.config = config;
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	private void init() {
		map = new GraphToFlatTranformer().transform(graph);
		Transformer<Vertex, Point2D> vertexTrasformer = TransformerUtils.mapTransformer(map);
		Layout<Vertex, Unit<Edge>> layout = new StaticLayout<Vertex, Unit<Edge>>(graph, vertexTrasformer);

		Dimension d = new Dimension(640, 480);
        vm = new DefaultVisualizationModel<Vertex, Unit<Edge>>(layout, d);
        vv = new VisualizationViewer<Vertex, Unit<Edge>>(vm, d);
        GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv);
        add(gzsp, BorderLayout.CENTER);


        Factory<Vertex> vertexFactory = new Factory<Vertex>() { // My vertex factory
            public Vertex create() {
            	Vertex vertex = new VertexRoom(new Room());
            	map.put(vertex, new Point(0, 0));
                return vertex;
            }
        };
        Factory<Unit<Edge>> edgeFactory = new Factory<Unit<Edge>>() { // My edge factory
            public Unit<Edge> create() {
                return new Unit<Edge>(null);
            }
        };

        RoomEditingModalGraphMouse<Vertex, Unit<Edge>> graphMouse = new RoomEditingModalGraphMouse<Vertex, Unit<Edge>>(vv.getRenderContext()
        		, vertexFactory, edgeFactory, (float) config.getScaleFactor(), new CreateEdgeControlImpl(this, graph));

        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());

//        JMenuBar menubar = new JMenuBar();
//        menubar.add(graphMouse.getModeMenu());
//        gzsp.setCorner(menubar);


        initVertexView();
        initEdgeView();

        scaler = new CrossoverScalingControl();
	}

	/**
	 * set shape and icon transformers for vertexes, and set tooltip
	 */
	private void initVertexView() {
		roomShapeTransformer = new RoomShapeTransformer(config.getRoomWidth(), config.getRoomHeight());
        vv.getRenderContext().setVertexShapeTransformer(roomShapeTransformer);

        roomVertexIconTranformer = new RoomVertexIconTranformer(vv.getPickedVertexState(), config);
 		vv.getRenderContext().setVertexIconTransformer(roomVertexIconTranformer);

 		vv.setVertexToolTipTransformer(new ToStringLabeller<Vertex>());
	}

	/**
	 * set stroke and color transformers for edges, and set tooltip
	 */
	private void initEdgeView() {
		PickedState<Unit<Edge>> pse = vv.getPickedEdgeState();
        vv.getRenderContext().setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<Unit<Edge>>(pse, Color.black
        		, Color.cyan));
        vv.getRenderContext().setArrowDrawPaintTransformer(new PickableEdgePaintTransformer<Unit<Edge>>(pse, Color.black
        		, Color.cyan));
        vv.getRenderContext().setArrowFillPaintTransformer(new PickableEdgePaintTransformer<Unit<Edge>>(pse, Color.black
        		, Color.cyan));

        // Set up a new stroke Transformer for the edges
//        float dash[] = {10.0f};
//        final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
//        Transformer<Edge, Stroke> edgeStrokeTransformer = new Transformer<Edge, Stroke>() {
//            public Stroke transform(Edge s) {
//                return edgeStroke;
//            }
//        };
//        vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);

        vv.setEdgeToolTipTransformer(new ToStringLabeller<Unit<Edge>>());
	}

	public void magnify() {
		roomShapeTransformer.setScale(roomShapeTransformer.getScaleX() * getConfig().getScaleFactor());
		roomVertexIconTranformer.setScale(roomVertexIconTranformer.getScale() * getConfig().getScaleFactor());
		scaler.scale(vv, (float) getConfig().getScaleFactor(), vv.getCenter());
	}

	public void decrease() {
		roomShapeTransformer.setScale(roomShapeTransformer.getScaleX() / getConfig().getScaleFactor());
		roomVertexIconTranformer.setScale(roomVertexIconTranformer.getScale() / getConfig().getScaleFactor());
		scaler.scale(vv, (float) (1 / getConfig().getScaleFactor()), vv.getCenter());
	}

	public void addPickedEdgeStateItemListener(ItemListener listener) {
		vv.getPickedEdgeState().addItemListener(listener);
	}

	public void removePickedEdgeStateItemListener(ItemListener listener) {
		vv.getPickedEdgeState().removeItemListener(listener);
	}

	public void addPickedVertexStateItemListener(ItemListener listener) {
		vv.getPickedVertexState().addItemListener(listener);
	}

	public void removePickedVertexStateItemListener(ItemListener listener) {
		vv.getPickedVertexState().removeItemListener(listener);
	}

	public void deletePickedElements() {
		for (Vertex v : vv.getPickedVertexState().getPicked()) {
			if (v instanceof VertexSpecial) {
				LOG.warn("Special vertex is not deleting");
			} else {
				graph.removeVertex(v);
			}
		}
		for (Unit<Edge> ue : vv.getPickedEdgeState().getPicked()) {
			graph.removeEdge(ue);
		}
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
