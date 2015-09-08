package ru.intech.ussd.modeler.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.TransformerUtils;

import ru.intech.ussd.modeler.control.CreateEdgeControlImpl;
import ru.intech.ussd.modeler.control.RoomEditingModalGraphMouse;
import ru.intech.ussd.modeler.entities.Room;
import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.graphobjects.VertexRoom;
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
	private static final float K = 1.25f;

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private Graph<Vertex, Unit<Edge>> graph;
	private Map<Vertex, Point2D> map;
	private VisualizationModel<Vertex, Unit<Edge>> vm;
	private VisualizationViewer<Vertex, Unit<Edge>> vv;
	private GraphZoomScrollPane gzsp;
	private ScalingControl scaler;
	RoomShapeTransformer roomShapeTransformer;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public GraphPanel(Graph<Vertex, Unit<Edge>> graph, ) {
		this.graph = graph;
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================

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
        gzsp = new GraphZoomScrollPane(vv);


//        final PickedState<Vertex> ps = vv.getPickedVertexState();


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
        		, vertexFactory, edgeFactory, K, new CreateEdgeControlImpl(this, graph));

        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());

//        JMenuBar menubar = new JMenuBar();
//        menubar.add(graphMouse.getModeMenu());
//        gzsp.setCorner(menubar);


        // задаем форму и отображение для вершины
        RoomShapeTransformer roomShapeTransformer = new RoomShapeTransformer(width, height);
        vv.getRenderContext().setVertexShapeTransformer(roomShapeTransformer);

 		vv.getRenderContext().setVertexIconTransformer(new RoomVertexIconTranformer(vv.getPickedVertexState()));


        PickedState<Unit<Edge>> pes = vv.getPickedEdgeState();
        vv.getRenderContext().setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<Unit<Edge>>(pes, Color.black, Color.cyan));
        vv.getRenderContext().setArrowDrawPaintTransformer(new PickableEdgePaintTransformer<Unit<Edge>>(pes, Color.black, Color.cyan));
        vv.getRenderContext().setArrowFillPaintTransformer(new PickableEdgePaintTransformer<Unit<Edge>>(pes, Color.black, Color.cyan));

        // Set up a new stroke Transformer for the edges
        float dash[] = {10.0f};
        final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        Transformer<Edge, Stroke> edgeStrokeTransformer = new Transformer<Edge, Stroke>() {
            public Stroke transform(Edge s) {
                return edgeStroke;
            }
        };
//        vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);


        // add my listeners for ToolTips
        vv.setVertexToolTipTransformer(new ToStringLabeller<Vertex>());
        vv.setEdgeToolTipTransformer(new ToStringLabeller<Unit<Edge>>());


        scaler = new CrossoverScalingControl();


        JButton btnPlus = new JButton("+");
        btnPlus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	width *= K;
            	height *= K;
            	fontSize *= K;
                scaler.scale(vv, K, vv.getCenter());
                System.out.println("dimension [ " + width + ", " + height + " ]");
            }
        });
        JButton btnMinus = new JButton("-");
        btnMinus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	width /= K;
            	height /= K;
            	fontSize /= K;
                scaler.scale(vv, 1/K, vv.getCenter());
                System.out.println("dimension [ " + width + ", " + height + " ]");
            }
        });
	}

	public void magnify() {
		scaler.scale(vv, K, vv.getCenter());
	}

	public void decrease() {
		scaler.scale(vv, 1/K, vv.getCenter());
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

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
