package ru.intech.ussd.modeler;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.TransformerUtils;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.log4j.PropertyConfigurator;

import ru.intech.ussd.modeler.control.CreateEdgeControlImpl;
import ru.intech.ussd.modeler.control.RoomEditingModalGraphMouse;
import ru.intech.ussd.modeler.control.VertexAndEdgeControl;
import ru.intech.ussd.modeler.entities.Room;
import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.graphobjects.VertexRoom;
import ru.intech.ussd.modeler.services.GraphService;
import ru.intech.ussd.modeler.transformers.GraphToFlatTranformer;
import ru.intech.ussd.modeler.transformers.RoomVertexIconTranformer;
import ru.intech.ussd.modeler.ui.InfoPanel;
import ru.intech.ussd.modeler.util.Unit;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class Main {
//	private static float scale = 1.0f;
	private static final float K = 1.25f;

	public static double width = 150.0;
	public static double height = 50.0;

	public static float fontSize = 10.0f;

	public static void main(String[] args) {
		PropertyConfigurator.configure(Main.class.getResource("/log4j.properties").getPath());


		JFrame frame = new JFrame("Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());



		final Graph<Vertex, Unit<Edge>> g =  new ObservableGraph<Vertex, Unit<Edge>>(GraphService.loadGraph("333"));


		((ObservableGraph<Vertex, Unit<Edge>>)g).addGraphEventListener(new VertexAndEdgeControl());



		final Map<Vertex, Point2D> map = new GraphToFlatTranformer().transform(g);

		Dimension d = new Dimension(640, 480);

		Transformer<Vertex, Point2D> vertexTrasformer = TransformerUtils.mapTransformer(map);

		Layout<Vertex, Unit<Edge>> layout = new StaticLayout<Vertex, Unit<Edge>>(g, vertexTrasformer);

        VisualizationModel<Vertex, Unit<Edge>> vm = new DefaultVisualizationModel<Vertex, Unit<Edge>>(layout, d);

        final VisualizationViewer<Vertex, Unit<Edge>> vv = new VisualizationViewer<Vertex, Unit<Edge>>(vm, d);

        GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv);


        final PickedState<Vertex> ps = vv.getPickedVertexState();


        Factory<Vertex> vertexFactory = new Factory<Vertex>() { // My vertex factory
            public Vertex create() {
            	Vertex vertex = new VertexRoom(new Room());
            	map.put(vertex, new Point(0, 0));
                return vertex;
            }
        };
        Factory<Unit<Edge>> edgeFactory = new Factory<Unit<Edge>>() { // My edge factory
            public Unit<Edge> create() {
                return new Unit(null);
            }
        };

        RoomEditingModalGraphMouse<Vertex, Unit<Edge>> graphMouse = new RoomEditingModalGraphMouse<Vertex, Unit<Edge>>(vv.getRenderContext()
        		, vertexFactory, edgeFactory, K, new CreateEdgeControlImpl(frame, g));

        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());

        JMenuBar menubar = new JMenuBar();
        menubar.add(graphMouse.getModeMenu());
        gzsp.setCorner(menubar);


        // задаем форму и отображение для вершины
        @SuppressWarnings("unchecked")
		Transformer<Vertex,Shape> squares = new ConstantTransformer(new Rectangle2D.Double(-width/2, -height/2, width, height));
        vv.getRenderContext().setVertexShapeTransformer(squares);

 		vv.getRenderContext().setVertexIconTransformer(new RoomVertexIconTranformer(vv.getPickedVertexState()));


        PickedState<Unit<Edge>> pes = vv.getPickedEdgeState();
        vv.getRenderContext().setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<Unit<Edge>>(pes, Color.black, Color.cyan));
        vv.getRenderContext().setArrowDrawPaintTransformer(new PickableEdgePaintTransformer<Unit<Edge>>(pes, Color.black, Color.cyan));
        vv.getRenderContext().setArrowFillPaintTransformer(new PickableEdgePaintTransformer<Unit<Edge>>(pes, Color.black, Color.cyan));

        // Set up a new stroke Transformer for the edges
        float dash[] = {10.0f};
        final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
             BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        Transformer<Edge, Stroke> edgeStrokeTransformer = new Transformer<Edge, Stroke>() {
            public Stroke transform(Edge s) {
                return edgeStroke;
            }
        };
//        vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);


        // add my listeners for ToolTips
        vv.setVertexToolTipTransformer(new ToStringLabeller<Vertex>());
        vv.setEdgeToolTipTransformer(new ToStringLabeller<Unit<Edge>>());


        final ScalingControl scaler = new CrossoverScalingControl();


        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	width *= K;
            	height *= K;
            	fontSize *= K;
                scaler.scale(vv, K, vv.getCenter());
                System.out.println("dimension [ " + width + ", " + height + " ]");
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	width /= K;
            	height /= K;
            	fontSize /= K;
                scaler.scale(vv, 1/K, vv.getCenter());
                System.out.println("dimension [ " + width + ", " + height + " ]");
            }
        });

        JToolBar toolBar = new JToolBar();
        toolBar.add(plus);
        toolBar.add(minus);

        System.out.println(vv.getLayout());

        final InfoPanel infoPanel = new InfoPanel();

        vv.getPickedEdgeState().addItemListener(new ItemListener() {

			public void itemStateChanged(final ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							infoPanel.showEdge((Edge) e.getItem());
						}
					});
				}
				System.out.println("item state changed : " + e);
			}
		});
        vv.getPickedVertexState().addItemListener(new ItemListener() {

			public void itemStateChanged(final ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							infoPanel.showVertex((Vertex) e.getItem());
						}
					});
				}
				System.out.println("item state changed : " + e);
			}
		});

		frame.getContentPane().add(gzsp, BorderLayout.CENTER);
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
		frame.getContentPane().add(infoPanel, BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
	}



	// =================================================================================================================
	// Constants
	// =================================================================================================================

	// =================================================================================================================
	// Fields
	// =================================================================================================================

	// =================================================================================================================
	// Constructors
	// =================================================================================================================

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================

	// =================================================================================================================
	// Methods
	// =================================================================================================================

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================

}

