package ru.intech.ussd.modeler.control;

import edu.uci.ics.jung.graph.util.EdgeType;

public interface CreateEdgeControl<V> {
	boolean veto(EdgeType edgeType, V startVertex, V finsihVertex);
}
