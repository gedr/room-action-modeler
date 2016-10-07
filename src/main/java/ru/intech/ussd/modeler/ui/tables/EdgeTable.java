package ru.intech.ussd.modeler.ui.tables;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import ru.intech.ussd.modeler.events.EdgeEvent;
import ru.intech.ussd.modeler.events.EventBusHolder;
import ru.intech.ussd.modeler.events.EventType;
import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.EdgeAction;
import ru.intech.ussd.modeler.graphobjects.EdgeStart;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.util.Unit;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

import edu.uci.ics.jung.graph.Graph;

public class EdgeTable implements TableModel {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final String[] COLUMN_NAMES = {"Stat", "Type", "Id", "Description", "Key", "Active"};
	private static final Class<?>[] COLUMN_TYPES = {String.class, String.class, Integer.class, String.class, String.class, Boolean.class};

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private List<Edge> edges;
	private Graph<Vertex, Unit<Edge>> graph;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public EdgeTable() {
		EventBusHolder.getEventBus().register(this);
	}

	public EdgeTable(Graph<Vertex, Unit<Edge>> graph) {
		this();
		setGraph(graph);
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	@Override
	public int getRowCount() {
		return edges == null ? 0 : edges.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return COLUMN_NAMES[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return COLUMN_TYPES[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return "stat";
		}
		Edge e = edges.get(rowIndex);
		if (e instanceof EdgeAction) {
			EdgeAction ea = (EdgeAction) e;
			switch (columnIndex) {
				case 1 :
					return "A";
				case 2 :
					return ea.getId();
				case 3 :
					return ea.getDescription();
				case 4 :
					return ea.getKey();
				case 5 :
					return ea.isActive();
			}
		}
		if (e instanceof EdgeStart) {
			EdgeStart es = (EdgeStart) e;
			switch (columnIndex) {
				case 1 :
					return "S";
				case 2 :
					return es.getId();
				case 3 :
					return es.getDescription();
				case 4 :
					return es.getKey();
				case 5 :
					return es.isActive();
			}
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub

	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public Graph<Vertex, Unit<Edge>> getGraph() {
		return graph;
	}

	public void setGraph(Graph<Vertex, Unit<Edge>> graph) {
		this.graph = graph;
		extractEdgesFromGraph(graph);
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	public static JTable createSwingTable(Graph<Vertex, Unit<Edge>> graph) {
		EdgeTable et = new EdgeTable(graph);
		JTable tbl = new JTable(et);
		tbl.setAutoCreateRowSorter(true);
		tbl.getColumnModel().getColumn(0).setMaxWidth(50);
		tbl.getColumnModel().getColumn(1).setMaxWidth(50);
		tbl.getColumnModel().getColumn(2).setMaxWidth(75);
		tbl.getColumnModel().getColumn(5).setMaxWidth(50);
		return tbl;
	}

	private void extractEdgesFromGraph(Graph<Vertex, Unit<Edge>> graph) {
		List<Edge> lvr = new ArrayList<Edge>();
		if ((graph != null) && (graph.getVertexCount() > 0)) {
			for (Unit<Edge> e : graph.getEdges()) {
				if ((e.getValue() instanceof EdgeAction) || (e.getValue() instanceof EdgeStart)) {
					lvr.add(e.getValue());
				}
			}
		}
		edges = lvr;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void EventHandler(EdgeEvent event) {
//		System.out.println(event.getEdge().getClass().getName());
//		System.out.println(((EdgeAction) event.getEdge()).getId());

		if ((event.getEdge() instanceof EdgeStart) || (event.getEdge() instanceof EdgeAction)) {
			if (event.getEventType().equals(EventType.ADD)) {
				edges.add(event.getEdge());
			}
			if (event.getEventType().equals(EventType.REMOVE)) {
				edges.remove(event.getEdge());
			}
		}
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
