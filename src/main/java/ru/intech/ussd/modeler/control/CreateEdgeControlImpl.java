package ru.intech.ussd.modeler.control;

import java.awt.Component;

import javax.swing.JOptionPane;

import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.graphobjects.VertexFinish;
import ru.intech.ussd.modeler.graphobjects.VertexSpecial;
import ru.intech.ussd.modeler.graphobjects.VertexStart;
import ru.intech.ussd.modeler.util.Unit;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class CreateEdgeControlImpl implements CreateEdgeControl<Vertex> {
	// =================================================================================================================
	// Constants
	// =================================================================================================================

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private Component  parent;
	private Graph<Vertex, Unit<Edge>> graph;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public CreateEdgeControlImpl(Component parent, Graph<Vertex, Unit<Edge>> graph) {
		this.parent = parent;
		this.graph = graph;
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	public boolean veto(EdgeType edgeType, Vertex startVertex, Vertex finsihVertex) {
		return vetoEdgeBetweenStartAndFinishVertexes(edgeType, startVertex, finsihVertex)
				&& vetoVertexFinishNotBegin(edgeType, startVertex)
				&& vetoVertexStartNotEnd(edgeType, finsihVertex)
				&& vetoEdgeAlreadyExists(edgeType, startVertex, finsihVertex);
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public Component getParent() {
		return parent;
	}

	public void setParent(Component parent) {
		this.parent = parent;
	}

	public Graph<Vertex, Unit<Edge>> getGraph() {
		return graph;
	}

	public void setGraph(Graph<Vertex, Unit<Edge>> graph) {
		this.graph = graph;
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	private boolean vetoEdgeBetweenStartAndFinishVertexes(EdgeType edgeType, Vertex startVertex, Vertex finsihVertex) {
		if ((startVertex instanceof VertexSpecial) && (finsihVertex instanceof VertexSpecial)) {
			JOptionPane.showMessageDialog(parent, "Невозможно соединить эти вершины", "Ошибка", JOptionPane.ERROR_MESSAGE);
			parent.revalidate();
			parent.repaint();
			return false;
		}
		return true;
	}

	private boolean vetoVertexFinishNotBegin(EdgeType edgeType, Vertex startVertex) {
		if (startVertex instanceof VertexFinish) {
			JOptionPane.showMessageDialog(parent, "Финишная вершина не может быть начальной вершиной для ребра",
					"Ошибка", JOptionPane.ERROR_MESSAGE);
			parent.revalidate();
			parent.repaint();
			return false;
		}
		return true;
	}

	private boolean vetoVertexStartNotEnd(EdgeType edgeType, Vertex finsihVertex) {
		if (finsihVertex instanceof VertexStart) {
			JOptionPane.showMessageDialog(parent, "Стартовая вершина не может быть конечной вершиной для ребра",
					"Ошибка", JOptionPane.ERROR_MESSAGE);
			parent.revalidate();
			parent.repaint();
			return false;
		}
		return true;
	}

	private boolean vetoEdgeAlreadyExists(EdgeType edgeType, Vertex startVertex, Vertex finsihVertex) {
		for (Unit<Edge> uedge : graph.getOutEdges(startVertex)) {
			if (graph.getOpposite(startVertex, uedge).equals(finsihVertex)) {
				int res = JOptionPane.showConfirmDialog(parent, "Вершины уже соеденены. \n Добавить еще одно ?", "Вопрос",
						JOptionPane.YES_NO_OPTION);
				if (JOptionPane.YES_OPTION == res) {
					return true;
				} else {
					parent.revalidate();
					parent.repaint();
					return false;
				}
			}
		}
		return true;
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
