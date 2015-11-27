package ru.intech.ussd.modeler.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.intech.ussd.modeler.config.GraphConfig;
import ru.intech.ussd.modeler.control.VertexAndEdgeControl;
import ru.intech.ussd.modeler.dao.UssdDaoManager;
import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.graphobjects.VertexFinish;
import ru.intech.ussd.modeler.graphobjects.VertexStart;
import ru.intech.ussd.modeler.services.GraphService;
import ru.intech.ussd.modeler.util.Unit;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;

public class MainFrame extends JFrame implements ActionListener, ItemListener {
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

	private Timer timer = new Timer(750, this);

	private int countSelectedVertexes = 0;
	private int countSelectedEdges = 0;
	private List<Object> selectedItem = new ArrayList<Object>();

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
		init();
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			JButton btn = (JButton) e.getSource();
			if (ACTION_CMD_MAGNIFY.equals(btn.getActionCommand())) {
				graphPanel.magnify();
			} else if (ACTION_CMD_DECREASE.equals(btn.getActionCommand())) {
				graphPanel.decrease();
			} else if (ACTION_CMD_SAVE.equals(btn.getActionCommand())) {
				SwingUtilities.invokeLater(new Runnable() {
				    public void run() {
				    	LOG.debug("save graph");
						graphPanel.savePositions();
						GraphService.saveGraph(graph, false, vertexAndEdgeControl.getRemovedVertexes()
								, vertexAndEdgeControl.getRemovedEdges());
				    }
				});
			} else if (ACTION_CMD_CHECK.equals(btn.getActionCommand())) {
				GraphService.checkGraph(graph);
			} else if (ACTION_CMD_DELETE.equals(btn.getActionCommand())) {
				graphPanel.deletePickedElements();
				invalidate();
				repaint();
			} else if (ACTION_CMD_ADD.equals(btn.getActionCommand())) {
		        String s = JOptionPane.showInputDialog("Название нового сервиса");
		        cb.addItem(s);
		        graph = new DirectedSparseGraph<Vertex, Unit<Edge>>();
		        graph.addVertex(new VertexStart());
		        graph.addVertex(new VertexFinish());
		        graph = new ObservableGraph<Vertex, Unit<Edge>>(graph);
		        graphPanel.setVisible(false);
		        graphPanel = new GraphPanel(graph, config);
		        getContentPane().add(graphPanel, BorderLayout.CENTER);
			} else if (ACTION_CMD_MARK.equals(btn.getActionCommand())) {
				String s = JOptionPane.showInputDialog("Название нового сервиса");
				if (StringUtils.isNotBlank(s)) {
					graphPanel.mark(s);
				}
			} else if (ACTION_CMD_LOAD.equals(btn.getActionCommand())) {
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


//				for (Unit<Edge> ue : graph.getEdges()) {
//					String sss = "unknown";
//					if (ue.getValue() instanceof EdgeStart) {
//						sss = ((EdgeStart) ue.getValue()).toString();
//					}
//					if (ue.getValue() instanceof EdgeAction) {
//						sss = ((EdgeAction) ue.getValue()).toString();
//					}
//					if (ue.getValue() instanceof EdgeFinish) {
//						sss = ((EdgeFinish) ue.getValue()).toString();
//					}
//
//					Pair<Vertex> pr = graph.getEndpoints(ue);
//					String v1 = "unknown";
//					String v2 = "unknown";
//
//					if (pr.getFirst() instanceof VertexRoom) {
//						v1 = "room #" + ((VertexRoom) pr.getFirst()).getRoom().getId();
//					}
//					if (pr.getFirst() instanceof VertexStart) {
//						v1 = ((VertexStart) pr.getFirst()).toString();
//					}
//					if (pr.getFirst() instanceof VertexFinish) {
//						v1 = ((VertexFinish) pr.getFirst()).toString();
//					}
//
//					if (pr.getSecond() instanceof VertexRoom) {
//						v2 = "room #" + ((VertexRoom) pr.getSecond()).getRoom().getId();
//					}
//					if (pr.getSecond() instanceof VertexStart) {
//						v2 = ((VertexStart) pr.getSecond()).toString();
//					}
//					if (pr.getSecond() instanceof VertexFinish) {
//						v2 = ((VertexFinish) pr.getSecond()).toString();
//					}
//
//					System.out.println(v1 + "  --" + sss + "--  " + v2);
//				}


			}
		}

		if (e.getSource() instanceof Timer) {
			((Timer) e.getSource()).stop();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (selectedItem.isEmpty()) {
						infoPanel.showEmpty();
					} else {
						Object o = selectedItem.get(selectedItem.size() - 1);
						if (o instanceof Vertex) {
							infoPanel.showVertex((Vertex) o);
						} else {
							@SuppressWarnings("unchecked")
							Unit<Edge> ue = (Unit<Edge>) o;
							infoPanel.showEdge(ue.getValue());
						}
					}
					lblInfo.setText("V : " + countSelectedVertexes + " ; E : " + countSelectedEdges);
				}
			});

		}
	}

	public void itemStateChanged(final ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (e.getItem() instanceof Vertex) {
						countSelectedVertexes++;
						infoPanel.showVertex((Vertex) e.getItem());
					} else {
						countSelectedEdges++;
						@SuppressWarnings("unchecked")
						Unit<Edge> ue = (Unit<Edge>) e.getItem();
						infoPanel.showEdge(ue.getValue());
					}
					selectedItem.add(e.getItem());
					lblInfo.setText("V : " + countSelectedVertexes + " ; E : " + countSelectedEdges);
				}
			});
		}
		if (e.getStateChange() == ItemEvent.DESELECTED) {
			System.out.println("DESELECTED " + e) ;
			if (e.getItem() instanceof Vertex) {
				countSelectedVertexes--;
			} else {
				countSelectedEdges--;
			}
			boolean res = selectedItem.remove(e.getItem());
			System.out.println("deselect res = " + res + "  ;  selectedItem.size = " + selectedItem.size());
			timer.start();
		}

		LOG.info("item state changed : {}", e);
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
		graphPanel.addPickedEdgeStateItemListener(this);
		graphPanel.addPickedVertexStateItemListener(this);

		getContentPane().add(graphPanel, BorderLayout.CENTER);
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





	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
