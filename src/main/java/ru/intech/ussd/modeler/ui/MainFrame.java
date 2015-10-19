package ru.intech.ussd.modeler.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.intech.ussd.modeler.config.GraphConfig;
import ru.intech.ussd.modeler.control.VertexAndEdgeControl;
import ru.intech.ussd.modeler.dao.UssdDaoManager;
import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.services.GraphService;
import ru.intech.ussd.modeler.util.Unit;
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

	private static final Logger LOG = LoggerFactory.getLogger(MainFrame.class);


	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private Graph<Vertex, Unit<Edge>> graph;
	private GraphConfig config;
	private GraphPanel graphPanel;
	private VertexAndEdgeControl vertexAndEdgeControl;
	private InfoPanel infoPanel;

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
				GraphService.saveGraph(graph, false, vertexAndEdgeControl.getRemovedVertexes()
						, vertexAndEdgeControl.getRemovedEdges());
			} else if (ACTION_CMD_CHECK.equals(btn.getActionCommand())) {
				GraphService.checkGraph(graph);
			} else if (ACTION_CMD_DELETE.equals(btn.getActionCommand())) {
				graphPanel.deletePickedElements();
				invalidate();
				repaint();
			}
		}
	}

	public void itemStateChanged(final ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					if (e.getItem() instanceof Vertex) {
						infoPanel.showVertex((Vertex) e.getItem());
					} else {
						@SuppressWarnings("unchecked")
						Unit<Edge> ue = (Unit<Edge>) e.getItem();
						infoPanel.showEdge(ue.getValue());
					}
				}
			});
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
		getContentPane().add(infoPanel, BorderLayout.SOUTH);
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

        List<String> lst = UssdDaoManager.loadServices();
        lst.add(0, "");
        JComboBox<String> cb = new JComboBox<String>(lst.toArray(new String[0]));
        tb.add(cb);

        btn = new JButton("load");
        btn.setActionCommand(ACTION_CMD_LOAD);
        btn.addActionListener(this);
        tb.add(btn);

        btn = new JButton("add");
        String s = JOptionPane.showInputDialog("Название нового сервиса");
        cb.addItem(s);


        return tb;
	}




	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
