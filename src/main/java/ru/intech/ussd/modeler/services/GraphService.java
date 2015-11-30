package ru.intech.ussd.modeler.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.intech.ussd.modeler.dao.UssdDaoManager;
import ru.intech.ussd.modeler.entities.Action;
import ru.intech.ussd.modeler.entities.Attribute;
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
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class GraphService {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final Logger LOG = LoggerFactory.getLogger(GraphService.class);

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

		Graph<Vertex, Unit<Edge>> graph = new DirectedSparseGraph<Vertex, Unit<Edge>>();
		addVertexes(graph, eplst, aplst);
		addEdges(graph, eplst, aplst);
		return graph;
	}

	private static void addVertexes(Graph<Vertex, Unit<Edge>> graph, List<EntryPoint> lep, List<Action> la) {
		Set<Room> cr = new HashSet<Room>();

		for (EntryPoint it : lep) {
			cr.add(it.getRoom());
		}

		for (Action it : la) {
			cr.add(it.getCurrentRoom());
			cr.add(it.getNextRoom());
		}
		for (Room it : cr) {
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
			if ((it instanceof VertexRoom)
					&& (graph.getOutEdges(it).isEmpty() || ((VertexRoom) it).isFinish())) {
				graph.addEdge(new Unit<Edge>(new EdgeFinish()), it, finish, EdgeType.DIRECTED);
			}
		}
	}

	private static Vertex findVertex(Room room, Graph<Vertex, Unit<Edge>> grap) {
		for (Vertex it : grap.getVertices()) {
			if ((it instanceof VertexRoom) && Objects.equals(room, ((VertexRoom) it).getRoom())) {
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

	public static void saveGraph(Graph<Vertex, Unit<Edge>> graph, boolean ignoreWarnings, Collection<Vertex> vertexForDelete
			, Collection<Edge> edgeForDelete) {
		List<Triple<Boolean, String, Object>> lst = checkGraph(graph);
		for (Triple<Boolean, String, Object> t : lst) {
			if (t.getLeft() || (ignoreWarnings && !t.getLeft())) {
				throw new IllegalStateException("graph have errors");
			}
		}
		String service = findService(graph);
		markVertexBeforeFinish(graph);
		deleteEdges(edgeForDelete); // first delete operation
		deleteVertexes(vertexForDelete); // second delete operation
		saveVertexes(graph);
		saveEdges(graph, service);
	}

	private static String findService(Graph<Vertex, Unit<Edge>> graph) {
		for (Unit<Edge> uedge : graph.getEdges()) {
			if (uedge != null) {
				if (uedge.getValue() instanceof EdgeStart) {
					EdgeStart es = (EdgeStart) uedge.getValue();
					if ((es.getEntryPoint() != null) && !StringUtils.isBlank(es.getEntryPoint().getService())) {
						return es.getEntryPoint().getService();
					}
				}
				if (uedge.getValue() instanceof EdgeAction) {
					EdgeAction ea = (EdgeAction) uedge.getValue();
					if ((ea.getAction() != null) && !StringUtils.isBlank(ea.getAction().getService())) {
						return ea.getAction().getService();
					}
				}
			}
		}
		return null;
	}

	private static void markVertexBeforeFinish(Graph<Vertex, Unit<Edge>> graph) {
		for (Vertex v : graph.getVertices()) {
			if (v instanceof VertexFinish) {
				for (Unit<Edge> e : graph.getInEdges(v)) {
					Vertex vi = graph.getOpposite(v, e);
					if (vi instanceof VertexRoom) {
						((VertexRoom) vi).setFinish(true);
					}
				}
				break;
			}
		}
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
				Attribute a = room.getAttribute();
				if (a != null) {
					if (a.getId() != null) {
						UssdDaoManager.updateAttribute(a);
					} else {
						a.setId(room.getId());
						UssdDaoManager.saveAttribute(a);
					}
				}
			}
		}
	}

	private static void saveEdges(Graph<Vertex, Unit<Edge>> graph, String service) {
		for (Unit<Edge> uedge : graph.getEdges()) {
			if ((uedge == null) || (uedge.getValue() == null)) {
				LOG.error("graph contain null edge");
				continue;
			}
			if (uedge.getValue().isChanged()) {
				uedge.getValue().applyChanges();
				saveEdgeStart(graph, uedge, service);
				saveEdgeAction(graph, uedge, service);
			}
		}
	}

	private static void saveEdgeStart(Graph<Vertex, Unit<Edge>> graph, Unit<Edge> uedge, String service) {
		if (uedge.getValue() instanceof EdgeStart) {
			EntryPoint ep = ((EdgeStart) uedge.getValue()).getEntryPoint();
			if (ep.getId() != null) {
				UssdDaoManager.updateEntryPoint(ep);
			} else {
				VertexRoom dst = (VertexRoom) graph.getDest(uedge);
				ep.setRoom(dst.getRoom());
				ep.setService(service);
				UssdDaoManager.saveEntryPoint(ep);
			}
		}
	}

	private static void saveEdgeAction(Graph<Vertex, Unit<Edge>> graph, Unit<Edge> uedge, String service) {
		if (uedge.getValue() instanceof EdgeAction) {
			Action a = ((EdgeAction) uedge.getValue()).getAction();
			if (a.getId() != null) {
				UssdDaoManager.updateAction(a);
			} else {
				VertexRoom src = (VertexRoom) graph.getSource(uedge);
				VertexRoom dst = (VertexRoom) graph.getDest(uedge);
				a.setCurrentRoom(src.getRoom());
				a.setNextRoom(dst.getRoom());
				a.setService(service);
				UssdDaoManager.saveAction(a);
			}
		}
	}

	public static List<Triple<Boolean, String, Object>> checkGraph(Graph<Vertex, Unit<Edge>> graph) {
		List<Triple<Boolean, String, Object>> lst = new ArrayList<Triple<Boolean, String, Object>>();
		lst.addAll(checkVertexes(graph));
		lst.addAll(checkEdges(graph));
		LOG.info(lst.toString());
		return lst;
	}

	private static List<Triple<Boolean, String, Object>> checkVertexes(Graph<Vertex, Unit<Edge>> graph) {
		List<Triple<Boolean, String, Object>> lst = new ArrayList<Triple<Boolean, String, Object>>();

		for (Vertex vertex : graph.getVertices()) {
			if (vertex instanceof VertexRoom ) {
				VertexRoom vr = (VertexRoom) vertex;
				if (StringUtils.isBlank(vr.getDescription())) {
					lst.add(Triple.of(false, "ПРЕДУПРЕЖДЕНИЕ: у вершины нет описания", (Object) vr));
				}
				if (StringUtils.isBlank(vr.getFunction())) {
					lst.add(Triple.of(true, "ОШИБКА: у вершины нет функции", (Object) vr));
				}
			}
		}
		return lst;
	}

	private static List<Triple<Boolean, String, Object>> checkEdges(Graph<Vertex, Unit<Edge>> graph) {
		List<Triple<Boolean, String, Object>> lst = new ArrayList<Triple<Boolean, String, Object>>();

		for (Unit<Edge> uedge : graph.getEdges()) {
			if ((uedge == null) || (uedge.getValue() == null)) {
					lst.add(Triple.of(true, "ОШИБКА: NULL ребро", (Object) uedge));
				continue;
			}
			if (uedge.getValue() instanceof EdgeStart) {
				EdgeStart es = (EdgeStart) uedge.getValue();
				if (StringUtils.isBlank(es.getKey())) {
					lst.add(Triple.of(true, "ОШИБКА: в стартовом ребре нет ключа перехода", (Object) uedge));
				}
				if (StringUtils.isBlank(es.getDescription())) {
					lst.add(Triple.of(false, "ПРЕДУПРЕЖДЕНИЕ: у стартового ребра нет описания", (Object) uedge));
				}
			}

			if ((uedge.getValue() instanceof EdgeAction)
					&& StringUtils.isBlank(((EdgeAction) uedge.getValue()).getDescription())) {
				lst.add(Triple.of(false, "ПРЕДУПРЕЖДЕНИЕ: у ребра нет описания", (Object) uedge));
			}

		}
		return lst;
	}

	private static void deleteEdges(Collection<Edge> edgeForDelete) {
		if ((edgeForDelete == null) || edgeForDelete.isEmpty()) {
			return;
		}
		List<Integer> epids = new ArrayList<Integer>();
		List<Integer> aids = new ArrayList<Integer>();
		for (Edge edge : edgeForDelete) {
			if ((edge instanceof EdgeStart) && (((EdgeStart) edge).getEntryPoint() != null)
					&& (((EdgeStart) edge).getEntryPoint().getId() != null)) {
				epids.add(((EdgeStart) edge).getEntryPoint().getId());
			}
			if ((edge instanceof EdgeAction) && (((EdgeAction) edge).getAction() != null)
					&& (((EdgeAction) edge).getAction().getId() != null)) {
				aids.add(((EdgeAction) edge).getAction().getId());
			}
		}
		if (!epids.isEmpty()) {
			UssdDaoManager.deleteEntryPointsById(epids);
		}
		if (!aids.isEmpty()) {
			UssdDaoManager.deleteActionsById(aids);
		}
	}

	private static void deleteVertexes(Collection<Vertex> vertexForDelete) {
		if ((vertexForDelete == null) || vertexForDelete.isEmpty()) {
			return;
		}
		List<Integer> rids = new ArrayList<Integer>();
		for (Vertex v : vertexForDelete) {
			if ((v instanceof VertexRoom) && (((VertexRoom) v).getRoom() != null)
					&& (((VertexRoom) v).getRoom().getId() != null)) {
				rids.add(((VertexRoom) v).getRoom().getId());
			}
		}
		if (!rids.isEmpty()) {
			UssdDaoManager.deleteRoomsById(rids);
		}
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
