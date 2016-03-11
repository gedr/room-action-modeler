package ru.intech.ussd.modeler.control;

import java.util.HashSet;
import java.util.Set;

import ru.intech.ussd.modeler.events.EdgeEvent;
import ru.intech.ussd.modeler.events.EventBusHolder;
import ru.intech.ussd.modeler.events.EventType;
import ru.intech.ussd.modeler.events.VertexEvent;
import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.EdgeAction;
import ru.intech.ussd.modeler.graphobjects.EdgeFinish;
import ru.intech.ussd.modeler.graphobjects.EdgeStart;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.graphobjects.VertexFinish;
import ru.intech.ussd.modeler.graphobjects.VertexRoom;
import ru.intech.ussd.modeler.graphobjects.VertexStart;
import ru.intech.ussd.modeler.util.Unit;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import edu.uci.ics.jung.graph.util.Pair;

public class VertexAndEdgeControl implements GraphEventListener<Vertex, Unit<Edge>> {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
//	private static final Logger LOG = LoggerFactory.getLogger(VertexAndEdgeControl.class);

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
				if (edge.getValue() == null) {
					edge = createEdge(evt.getSource(), edge);
				}
				EventBusHolder.getEventBus().post(new EdgeEvent(edge.getValue(), EventType.ADD));
				break;

			case EDGE_REMOVED :
				edge = ((GraphEvent.Edge<Vertex, Unit<Edge>>) evt).getEdge();
				if ((edge.getValue() instanceof EdgeStart) && (((EdgeStart) edge.getValue()).getEntryPoint() != null)
						&& (((EdgeStart) edge.getValue()).getEntryPoint().getId() != null)) {
					removedEdges.add(edge.getValue());
				}
				if ((edge.getValue() instanceof EdgeAction) && (((EdgeAction) edge.getValue()).getAction() != null)
						&& (((EdgeAction) edge.getValue()).getAction().getId() != null)) {
					removedEdges.add(edge.getValue());
				}
				EventBusHolder.getEventBus().post(new EdgeEvent(edge.getValue(), EventType.REMOVE));
				break;

			case VERTEX_ADDED :
				vertex = ((GraphEvent.Vertex<Vertex, Unit<Edge>>) evt).getVertex();
				EventBusHolder.getEventBus().post(new VertexEvent(vertex, EventType.ADD));
				break;

			case VERTEX_REMOVED :
				vertex = ((GraphEvent.Vertex<Vertex, Unit<Edge>>) evt).getVertex();
				if ((vertex instanceof VertexRoom) && (((VertexRoom) vertex).getRoom() != null)
						&& (((VertexRoom) vertex).getRoom().getId() != null)) {
					removedVertexes.add(vertex);
				}
				EventBusHolder.getEventBus().post(new VertexEvent(vertex, EventType.REMOVE));
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
	private Unit<Edge> createEdge(Graph<Vertex, Unit<Edge>> graph, Unit<Edge> edge) {
		Pair<Vertex> pair = graph.getEndpoints(edge);
		Vertex src = graph.isSource(pair.getFirst(), edge) ? pair.getFirst() : pair.getSecond();
		Vertex dst = graph.isDest(pair.getFirst(), edge) ? pair.getFirst() : pair.getSecond();

		if (dst instanceof VertexFinish) {
			edge.setValue(new EdgeFinish());
		} else if (src instanceof VertexStart) {
			EdgeStart e = new EdgeStart(null);
			e.setActive(true);
			e.setKey("unknown");
			edge.setValue(e);
		} else {
			EdgeAction edgeAction = new EdgeAction(null);
			edgeAction.setKey('-');
			edgeAction.setActive(true);
			edge.setValue(edgeAction);
		}
		return edge;
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
