package ru.intech.ussd.modeler.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.intech.ussd.modeler.config.GraphConfig;
import ru.intech.ussd.modeler.control.VertexAndEdgeControl;
import ru.intech.ussd.modeler.dao.UssdDaoManager;
import ru.intech.ussd.modeler.events.EdgeEvent;
import ru.intech.ussd.modeler.events.EventBusHolder;
import ru.intech.ussd.modeler.events.EventType;
import ru.intech.ussd.modeler.events.GraphEvent;
import ru.intech.ussd.modeler.events.VertexEvent;
import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.graphobjects.VertexFinish;
import ru.intech.ussd.modeler.graphobjects.VertexStart;
import ru.intech.ussd.modeler.services.GraphService;
import ru.intech.ussd.modeler.ui.tables.EdgeTable;
import ru.intech.ussd.modeler.ui.tables.ProjectionTable;
import ru.intech.ussd.modeler.ui.tables.VertexTable;
import ru.intech.ussd.modeler.util.Unit;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;

public class MainFrame extends JFrame implements ActionListener { //, ItemListener {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final long serialVersionUID = 1L;

	private static final String ACTION_CMD_MAGNIFY = "magnify";
	private static final String ACTION_CMD_DECREASE = "decrease";
	private static final String ACTION_CMD_SAVE = "save";
	private static final String ACTION_CMD_CHECK = "check";
	private static final String ACTION_CMD_DELETE = "delete";
	private static final String ACTION_CMD_LOAD = "load";
	private static final String ACTION_CMD_ADD = "add";
	private static final String ACTION_CMD_MARK = "mark";

	private static final Logger LOG = LoggerFactory.getLogger(MainFrame.class);

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private Graph<Vertex, Unit<Edge>> graph;
	private GraphConfig config;
	private GraphPanel graphPanel;
	private VertexAndEdgeControl vertexAndEdgeControl;
	private InfoPanel infoPanel;
	private JComboBox<String> cb;
	private JLabel lblInfo;

	private int countSelectedVertexes = 0;
	private int countSelectedEdges = 0;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public MainFrame(Graph<Vertex, Unit<Edge>> graph, GraphConfig config) {
		Validate.notNull(graph);
		this.graph = graph;
		if (!(this.graph instanceof ObservableGraph)) {
			this.graph = new ObservableGraph<Vertex, Unit<Edge>>(this.graph);
		}
		vertexAndEdgeControl = new VertexAndEdgeControl();
		((ObservableGraph<Vertex, Unit<Edge>>) this.graph).addGraphEventListener(vertexAndEdgeControl);

		this.config = config;
		EventBusHolder.getEventBus().register(this);
		init();
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	public void actionPerformed(ActionEvent e) {
		dispatchButtonActions(e);
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	private void init() {
		setTitle("Ussd graph editor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		infoPanel = new InfoPanel();
		graphPanel = new GraphPanel(graph, config);
//		graphPanel.addPickedEdgeStateItemListener(this);
//		graphPanel.addPickedVertexStateItemListener(this);

		JTabbedPane tbd = new JTabbedPane(JTabbedPane.LEFT);
		tbd.add(new JScrollPane(ProjectionTable.createSwingTable(graph)), 0);
		tbd.add(new JScrollPane(VertexTable.createSwingTable(graph)), 1);
		tbd.add(new JScrollPane(EdgeTable.createSwingTable(graph)), 2);

		// Create vertical labels to render tab titles
		JLabel tab1 = new JLabel(" Projection ");
		tab1.setUI(new VerticalLabelUI(false)); // true/false to make it upwards/downwards
		tbd.setTabComponentAt(0, tab1); // For component1

		JLabel tab2 = new JLabel(" Vertexes ");
		tab2.setUI(new VerticalLabelUI(false)); // true/false to make it upwards/downwards
		tbd.setTabComponentAt(1, tab2); // For component1

		JLabel tab3 = new JLabel(" Edges ");
		tab3.setUI(new VerticalLabelUI(false)); // true/false to make it upwards/downwards
		tbd.setTabComponentAt(2, tab3); // For component1

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setDividerLocation(0.5);
		split.setOneTouchExpandable(true);
		split.add(graphPanel, JSplitPane.LEFT);
		split.add(tbd, JSplitPane.RIGHT);


		getContentPane().add(split, BorderLayout.CENTER);
		getContentPane().add(initToolBar(), BorderLayout.NORTH);

		lblInfo = new JLabel("V : " + countSelectedVertexes + " ; E : " + countSelectedEdges);
		JPanel p = new JPanel(new BorderLayout());

		p.add(infoPanel, BorderLayout.CENTER);
		p.add(lblInfo, BorderLayout.SOUTH);
		getContentPane().add(p, BorderLayout.SOUTH);
	}

	private Component initToolBar() {
		JToolBar tb = new JToolBar();
        JButton btn = new JButton("+");
        btn.setActionCommand(ACTION_CMD_MAGNIFY);
        btn.addActionListener(this);
        tb.add(btn);
        btn = new JButton("-");
        btn.setActionCommand(ACTION_CMD_DECREASE);
        btn.addActionListener(this);
        tb.add(btn);
        btn = new JButton("save");
        btn.setActionCommand(ACTION_CMD_SAVE);
        btn.addActionListener(this);
        tb.add(btn);
        btn = new JButton("check");
        btn.setActionCommand(ACTION_CMD_CHECK);
        btn.addActionListener(this);
        tb.add(btn);
        btn = new JButton("delete");
        btn.setActionCommand(ACTION_CMD_DELETE);
        btn.addActionListener(this);
        tb.add(btn);
        btn = new JButton("*");
        btn.setActionCommand(ACTION_CMD_MARK);
        btn.addActionListener(this);
        tb.add(btn);

        List<String> lst = UssdDaoManager.loadServices();
        lst.add(0, "");
        cb = new JComboBox<String>(lst.toArray(new String[0]));
        tb.add(cb);

        btn = new JButton("load");
        btn.setActionCommand(ACTION_CMD_LOAD);
        btn.addActionListener(this);
        tb.add(btn);

        btn = new JButton("add");
        btn.setActionCommand(ACTION_CMD_ADD);
        btn.addActionListener(this);
        tb.add(btn);

        return tb;
	}

	private void save() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				LOG.debug("save graph");
				graphPanel.savePositions();
				GraphService.saveGraph(graph, false, vertexAndEdgeControl.getRemovedVertexes(),
						vertexAndEdgeControl.getRemovedEdges());
			}
		});
	}

	private void dispatchButtonActions(ActionEvent e) {
		if (!(e.getSource() instanceof JButton)) {
			return;
		}
		JButton btn = (JButton) e.getSource();

		switch (btn.getActionCommand()) {
			case ACTION_CMD_MAGNIFY :
				graphPanel.magnify();
				break;
			case ACTION_CMD_DECREASE :
				graphPanel.decrease();
				break;
			case ACTION_CMD_SAVE :
				save();
				break;
			case ACTION_CMD_CHECK :
				GraphService.checkGraph(graph);
				break;
			case ACTION_CMD_DELETE :
				graphPanel.deletePickedElements();
				invalidate();
				repaint();
				break;
			case ACTION_CMD_ADD :
				String s = JOptionPane.showInputDialog("Название нового сервиса");
				cb.addItem(s);
				graph = new DirectedSparseGraph<Vertex, Unit<Edge>>();
				graph.addVertex(new VertexStart());
				graph.addVertex(new VertexFinish());
				graph = new ObservableGraph<Vertex, Unit<Edge>>(graph);
				graphPanel.setVisible(false);
				graphPanel = new GraphPanel(graph, config);
				getContentPane().add(graphPanel, BorderLayout.CENTER);
				break;
			case ACTION_CMD_MARK :
				String mt = JOptionPane.showInputDialog("Текст для выделения");
				if (StringUtils.isNotBlank(mt)) {
					graphPanel.mark(mt);
				}
				break;
			case ACTION_CMD_LOAD :
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {
						List<Unit<Edge>> cue = new ArrayList<Unit<Edge>>(graph.getEdges());
						for (Unit<Edge> ue : cue) {
							graph.removeEdge(ue);
						}

						graphPanel.invalidate();
						graphPanel.repaint();
					}
				});
				break;

			default :
				break;
		}
	}

	@Subscribe
	@AllowConcurrentEvents
	public void EventHandler(GraphEvent event) {
		if (event instanceof VertexEvent) {
			VertexEvent e = (VertexEvent) event;
			if (EventType.SELECTED.equals(e.getEventType())) {
				countSelectedVertexes++;
				showCommonInfo();
			}
			if (EventType.DESELECTED.equals(e.getEventType())) {
				countSelectedVertexes--;
				showCommonInfo();
			}
		}
		if (event instanceof EdgeEvent) {
			EdgeEvent e = (EdgeEvent) event;
			if (EventType.SELECTED.equals(e.getEventType())) {
				countSelectedEdges++;
				showCommonInfo();
			}
			if (EventType.DESELECTED.equals(e.getEventType())) {
				countSelectedEdges--;
				showCommonInfo();
			}
		}
	}

	private void showCommonInfo() {
		lblInfo.setText("V : " + countSelectedVertexes + " ; E : " + countSelectedEdges);
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
