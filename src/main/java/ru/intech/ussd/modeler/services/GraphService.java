package ru.intech.ussd.modeler.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ru.intech.ussd.modeler.dao.UssdDaoManager;
import ru.intech.ussd.modeler.entities.Action;
import ru.intech.ussd.modeler.entities.EntryPoint;
import ru.intech.ussd.modeler.entities.Room;
import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.EdgeAction;
import ru.intech.ussd.modeler.graphobjects.EdgeFinish;
import ru.intech.ussd.modeler.graphobjects.EdgeStart;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.graphobjects.VertexFinish;
import ru.intech.ussd.modeler.graphobjects.VertexRoom;
import ru.intech.ussd.modeler.graphobjects.VertexStart;
import ru.intech.ussd.modeler.util.Unit;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class GraphService {

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
	public static Graph<Vertex, Unit<Edge>> loadGraph(String service) {
		List<EntryPoint> eplst = UssdDaoManager.loadEntryPointByService(service);
		List<Action> aplst = UssdDaoManager.loadActionByService(service);

		Graph<Vertex, Unit<Edge>> graph = new DirectedSparseMultigraph<Vertex, Unit<Edge>>();
		addVertexes(graph, eplst, aplst);
		addEdges(graph, eplst, aplst);
		return graph;
	}

	private static void addVertexes(Graph<Vertex, Unit<Edge>> graph, List<EntryPoint> lep, List<Action> la) {
		Set<Room> set = new HashSet<Room>();
		for (EntryPoint it : lep) {
			set.add(it.getRoom());
		}
		for (Action it : la) {
			set.add(it.getCurrentRoom());
			set.add(it.getNextRoom());
		}
		for (Room it : set) {
			graph.addVertex(new VertexRoom(it));
		}
	}

	private static void addEdges(Graph<Vertex, Unit<Edge>> graph, List<EntryPoint> lep, List<Action> la) {
		for (Action it : la) {
			Edge edge = new EdgeAction(it);
			Vertex v1 = findVertex(it.getCurrentRoom(), graph);
			Vertex v2 = findVertex(it.getNextRoom(), graph);
			graph.addEdge(new Unit<Edge>(edge), v1, v2, EdgeType.DIRECTED);
		}
		addStart(graph, lep, la);
		addFinish(graph, lep, la);
	}

	private static void addStart(Graph<Vertex, Unit<Edge>> graph, List<EntryPoint> lep, List<Action> la) {
		Vertex start = new VertexStart();
		graph.addVertex(start);

		for (EntryPoint it : lep) {
			Edge edge = new EdgeStart(it);
			Vertex v = findVertex(it.getRoom(), graph);
			graph.addEdge(new Unit<Edge>(edge), start, v, EdgeType.DIRECTED);
		}
	}

	private static void addFinish(Graph<Vertex, Unit<Edge>> graph, List<EntryPoint> lep, List<Action> la) {
		Vertex finish = new VertexFinish();
		graph.addVertex(finish);

		for (Vertex it : graph.getVertices()) {
			if ((it instanceof VertexRoom) && graph.getOutEdges(it).isEmpty()) {
				graph.addEdge(new Unit<Edge>(new EdgeFinish()), it, finish, EdgeType.DIRECTED);
			}
		}
	}

	private static Vertex findVertex(Room room, Graph<Vertex, Unit<Edge>> grap) {
		for (Vertex it : grap.getVertices()) {
			if ((it instanceof VertexRoom) && room.equals(((VertexRoom)it).getRoom())) {
				return it;
			}
		}
		throw new IllegalArgumentException();
	}

	public static void deleteEdges(Set<Edge> edges) {
		if ((edges == null) || edges.isEmpty()) {
			return;
		}
		Set<Integer> actionIds = new TreeSet<Integer>();
		Set<Integer> epIds = new TreeSet<Integer>();
		for (Edge edge : edges) {
			if (edge instanceof EdgeAction) {
				Action action = ((EdgeAction) edge).getAction();
				if ((action != null) && (action.getId() != null)) {
					actionIds.add(action.getId());
				}
			}
			if (edge instanceof EdgeStart) {
				EntryPoint ep = ((EdgeStart) edge).getEntryPoint();
				if ((ep != null) && (ep.getId() != null)) {
					epIds.add(ep.getId());
				}
			}
		}
		if (!actionIds.isEmpty()) {
			UssdDaoManager.deleteActionsById(actionIds);
		}
		if (!epIds.isEmpty()) {
			UssdDaoManager.deleteEntryPointsById(epIds);
		}
	}

	public static void deleteVertexes(Set<Vertex> vertexes) {
		if ((vertexes == null) || vertexes.isEmpty()) {
			return;
		}
		Set<Integer> ids = new TreeSet<Integer>();
		for (Vertex it : vertexes) {
			if ((it instanceof VertexRoom) && (((VertexRoom) it).getRoom().getId() != null)) {
				ids.add(((VertexRoom) it).getRoom().getId());
			}
		}
		if (!ids.isEmpty()) {
			UssdDaoManager.deleteRoomsById(ids);
		}
	}

	public static void saveGraph(Graph<Vertex, Unit<Edge>> graph) {
		saveVertexes(graph);
		saveEdges(graph);
	}

	private static void saveVertexes(Graph<Vertex, Unit<Edge>> graph) {
		for (Vertex vertex : graph.getVertices()) {
			if (vertex.isChanged() && (vertex instanceof VertexRoom)) {
				vertex.applyChanges();
				Room room = ((VertexRoom) vertex).getRoom();
				if (room.getId() != null) {
					UssdDaoManager.updateRoom(room);
				} else {
					UssdDaoManager.saveRoom(room);
				}
			}
		}
	}

	private static void saveEdges(Graph<Vertex, Unit<Edge>> graph) {
		for (Unit<Edge> uedge : graph.getEdges()) {

		}

	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
