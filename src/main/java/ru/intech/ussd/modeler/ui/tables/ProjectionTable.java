package ru.intech.ussd.modeler.ui.tables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import ru.intech.ussd.modeler.entities.Projection;
import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.graphobjects.VertexRoom;
import ru.intech.ussd.modeler.util.Unit;
import edu.uci.ics.jung.graph.Graph;

public class ProjectionTable implements TableModel {
	// =================================================================================================================
	// Constants
	// =================================================================================================================

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private Graph<Vertex, Unit<Edge>> graph;
	private List<MutablePair<Projection, Boolean>> projections = new ArrayList<MutablePair<Projection,Boolean>>();

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public ProjectionTable() {
	}

	public ProjectionTable(Graph<Vertex, Unit<Edge>> graph) {
		setGraph(graph);
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	@Override
	public int getRowCount() {
		return projections.size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return columnIndex == 0 ? "visible" : "name";
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columnIndex == 0 ? Boolean.class : String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Pair<Projection, Boolean> p = projections.get(rowIndex);
		if (columnIndex == 0) {
			return p.getRight();
		} else {
			return p.getLeft().getName();
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		MutablePair<Projection, Boolean> p = projections.get(rowIndex);
		if (columnIndex == 0) {
			p.setRight((boolean) aValue);
		} else {
			p.getLeft().setName((String) aValue);
		}
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
		extractProjectionsFromGraph(graph);
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	public static JTable createSwingTable(Graph<Vertex, Unit<Edge>> graph) {
		JTable tbl = new JTable(new ProjectionTable(graph));
		tbl.getColumnModel().getColumn(0).setMaxWidth(50);
		tbl.getColumnModel().getColumn(0).setMinWidth(10);
		return tbl;
	}

	public void addProjection(Projection p) {
		projections.add(MutablePair.of(p, Boolean.TRUE));
	}

	public void removeProjection(Projection p) {
		for (int i = projections.size() - 1; i >= 0; i--) {
			if (projections.get(i).getLeft().equals(p)) {
				projections.remove(i);
			}
		}
	}

	private void extractProjectionsFromGraph(Graph<Vertex, Unit<Edge>> graph) {
		Set<Projection> sp = new HashSet<Projection>();
		if ((graph != null) && (graph.getVertexCount() > 0)) {
			for (Vertex v : graph.getVertices()) {
				if ((v instanceof VertexRoom) && (((VertexRoom) v).getProjections() != null)) {
					sp.addAll(((VertexRoom) v).getProjections());
				}
			}
		}

		List<MutablePair<Projection, Boolean>> lst = new ArrayList<MutablePair<Projection, Boolean>>();
		for (Projection p : sp) {
			lst.add(MutablePair.of(p, Boolean.TRUE));
		}
		projections = lst;
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
