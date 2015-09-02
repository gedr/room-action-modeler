package ru.intech.ussd.modeler.control;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.graphobjects.VertexRoom;
import ru.intech.ussd.modeler.graphobjects.VertexSpecial;
import ru.intech.ussd.modeler.util.Unit;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import edu.uci.ics.jung.graph.util.Pair;

public class VertexAndEdgeControl implements GraphEventListener<Vertex, Unit<Edge>> {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final Logger LOG = LoggerFactory.getLogger(VertexAndEdgeControl.class);

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private Set<Edge> removedEdges = new HashSet<Edge>();
	private Set<Vertex> removedVertexes = new HashSet<Vertex>();

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public VertexAndEdgeControl() {
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	public void handleGraphEvent(GraphEvent<Vertex, Unit<Edge>> evt) {
		Unit<Edge> edge = null;
		Vertex vertex = null;

		switch (evt.getType()) {
			case EDGE_ADDED :
				edge = ((GraphEvent.Edge<Vertex, Unit<Edge>>) evt).getEdge();
				detectVirtualAction(evt.getSource(), edge);
				setRoomHeader(evt.getSource(), edge);
				LOG.debug("EDGE_ADDED : {}", edge);
				break;

			case EDGE_REMOVED :
				edge = ((GraphEvent.Edge<Vertex, Unit<Edge>>) evt).getEdge();
				if (edge.getAction().getId() != null) {
					removedEdges.add(edge);
				}
				LOG.debug("EDGE_REMOVED : {}", edge);
				break;

			case VERTEX_ADDED :
				vertex = ((GraphEvent.Vertex<Vertex, Unit<Edge>>) evt).getVertex();
				LOG.debug("VERTEX_ADDED : {}", vertex);
				break;

			case VERTEX_REMOVED :
				vertex = ((GraphEvent.Vertex<Vertex, Unit<Edge>>) evt).getVertex();
				if ((vertex instanceof VertexRoom) && (((VertexRoom) vertex).getRoom().getId() != null)) {
					removedVertexes.add(vertex);
				}
				LOG.debug("VERTEX_REMOVED : {}", vertex);
				break;
		}
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public Set<Edge> getRemovedEdges() {
		return removedEdges;
	}

	public Set<Vertex> getRemovedVertexes() {
		return removedVertexes;
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	private void detectVirtualAction(Graph<Vertex, Unit<Edge>> graph, Edge edge) {
		Pair<Vertex> pair = graph.getEndpoints(edge);
		if ((pair.getFirst() instanceof VertexSpecial) || (pair.getSecond() instanceof VertexSpecial)) {
			edge.setVirtualAction(true);
		}
	}

	private void setRoomHeader(Graph<Vertex, Unit<Edge>> graph, Edge edge) {
		Pair<Vertex> pair = graph.getEndpoints(edge);
		Vertex src = graph.isSource(pair.getFirst(), edge) ? pair.getFirst() : pair.getSecond();
		Vertex dst = graph.isDest(pair.getFirst(), edge) ? pair.getFirst() : pair.getSecond();
		if (src instanceof VertexRoom) {
			edge.getAction().setCurrentRoom(((VertexRoom) src).getRoom());
		}
		if (dst instanceof VertexRoom) {
			edge.getAction().setNextRoom(((VertexRoom) dst).getRoom());
		}
		edge.setKey("-");
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
