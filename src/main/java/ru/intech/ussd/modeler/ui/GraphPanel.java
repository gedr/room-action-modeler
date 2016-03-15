package ru.intech.ussd.modeler.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.TransformerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.intech.ussd.modeler.config.GraphConfig;
import ru.intech.ussd.modeler.control.CreateEdgeControlImpl;
import ru.intech.ussd.modeler.control.RoomEditingModalGraphMouse;
import ru.intech.ussd.modeler.entities.Attribute;
import ru.intech.ussd.modeler.entities.Projection;
import ru.intech.ussd.modeler.entities.Room;
import ru.intech.ussd.modeler.events.EdgeEvent;
import ru.intech.ussd.modeler.events.EventBusHolder;
import ru.intech.ussd.modeler.events.EventType;
import ru.intech.ussd.modeler.events.VertexEvent;
import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.graphobjects.VertexRoom;
import ru.intech.ussd.modeler.graphobjects.VertexSpecial;
import ru.intech.ussd.modeler.transformers.EdgeLabelTransformer;
import ru.intech.ussd.modeler.transformers.EdgeStrokeTransformer;
import ru.intech.ussd.modeler.transformers.GraphToFlatTransformer2;
import ru.intech.ussd.modeler.transformers.RoomShapeTransformer;
import ru.intech.ussd.modeler.transformers.RoomVertexIconTranformer;
import ru.intech.ussd.modeler.util.Unit;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class GraphPanel extends JPanel implements ItemListener {
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
	@Override
	public void itemStateChanged(final ItemEvent e) {
		if ((e.getStateChange() == ItemEvent.SELECTED) || (e.getStateChange() == ItemEvent.DESELECTED)) {
			if (e.getItem() instanceof Vertex) {
				EventBusHolder.getEventBus().post(new VertexEvent((Vertex) e.getItem(),
						e.getStateChange() == ItemEvent.SELECTED ? EventType.SELECTED : EventType.DESELECTED));
			} else {
				EventBusHolder.getEventBus().post(new EdgeEvent((Edge) ((Unit<?>) e.getItem()).getValue(),
								e.getStateChange() == ItemEvent.SELECTED ? EventType.SELECTED : EventType.DESELECTED));
			}
		}
	}

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
		map = new GraphToFlatTransformer2(config).transform(graph);
		Transformer<Vertex, Point2D> vertexTrasformer = TransformerUtils.mapTransformer(map);
		Layout<Vertex, Unit<Edge>> layout = new StaticLayout<Vertex, Unit<Edge>>(graph, vertexTrasformer);

		Dimension d = new Dimension(640, 480);
        vm = new DefaultVisualizationModel<Vertex, Unit<Edge>>(layout, d);
        vv = new VisualizationViewer<Vertex, Unit<Edge>>(vm, d);
        GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv);
        add(gzsp, BorderLayout.CENTER);


        Factory<Vertex> vertexFactory = new Factory<Vertex>() { // My vertex factory
            public Vertex create() {
            	Room r = new Room();
            	r.setAttribute(new Attribute());
            	r.getAttribute().setColor(config.getColorRoom());
            	Vertex vertex = new VertexRoom(r);
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
        vv.getPickedEdgeState().addItemListener(this);
        vv.getPickedVertexState().addItemListener(this);

        JMenuBar menubar = new JMenuBar();
        menubar.add(graphMouse.getModeMenu());
        gzsp.setCorner(menubar);

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
		PickableEdgePaintTransformer<Unit<Edge>> t = new PickableEdgePaintTransformer<Unit<Edge>>(vv.getPickedEdgeState(),
				Color.black, Color.cyan);
        vv.getRenderContext().setEdgeDrawPaintTransformer(t);
        vv.getRenderContext().setArrowDrawPaintTransformer(t);
        vv.getRenderContext().setArrowFillPaintTransformer(t);
        vv.getRenderContext().setEdgeStrokeTransformer(new EdgeStrokeTransformer());
        vv.getRenderContext().setEdgeLabelTransformer(new EdgeLabelTransformer());
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

	public Graph<Vertex, Unit<Edge>> copyGraph() {
		Graph<Vertex, Unit<Edge>> g = new DirectedSparseMultigraph<Vertex, Unit<Edge>>();

		for (Vertex v : graph.getVertices()) {
			g.addVertex(v);
		}

		for (Unit<Edge> e : graph.getEdges()) {
			Vertex src = graph.getSource(e);
			Vertex dst = graph.getDest(e);
			g.addEdge(e, src, dst, EdgeType.DIRECTED);
		}
		return g;
	}

	public Graph<Vertex, Unit<Edge>> applyProjections(Graph<Vertex, Unit<Edge>> g, Collection<Projection> lst) {
		if (lst == null) {
			return g;
		}
		List<Vertex> lv = new ArrayList<Vertex>();
		for (Vertex v : g.getVertices()) {
			if ((v instanceof VertexRoom)
					&& CollectionUtils.intersection(((VertexRoom) v).getProjections(), lst).isEmpty()) {
				lv.add(v);
			}
		}
		for (Vertex v : lv) {
			g.removeVertex(v);
		}
		return g;
	}

	public void mark(String s) {
		vv.getPickedVertexState().clear();
		for (Vertex v: graph.getVertices()) {
			if ((v instanceof VertexRoom) && (((VertexRoom) v).getDescription() != null)
					&& ((VertexRoom) v).getDescription().contains(s)) {
				vv.getPickedVertexState().pick(v, true);
			}
		}
	}

	public void savePositions() {
		for (Vertex v : graph.getVertices()) {
			if (v instanceof VertexRoom) {
				Point2D p = vv.getModel().getGraphLayout().transform(v);
				VertexRoom vr = (VertexRoom) v;
				Attribute a = vr.getAttribute();
				if (a == null) {
					a = new Attribute();
				}
				a.setX((int) p.getX());
				a.setY((int) p.getY());
				vr.setAttribute(a);
			}
		}
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}




