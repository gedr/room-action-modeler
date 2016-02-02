package ru.intech.ussd.modeler.transformers;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import ru.intech.ussd.modeler.config.GraphConfig;
import ru.intech.ussd.modeler.entities.Attribute;
import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.graphobjects.VertexFinish;
import ru.intech.ussd.modeler.graphobjects.VertexRoom;
import ru.intech.ussd.modeler.graphobjects.VertexSpecial;
import ru.intech.ussd.modeler.graphobjects.VertexStart;
import ru.intech.ussd.modeler.util.Unit;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class GraphToFlatTransformer2 implements Transformer<Graph<Vertex, Unit<Edge>>, Map<Vertex, Point2D>>{
	// =================================================================================================================
	// Constants
	// =================================================================================================================

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private GraphConfig config;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public GraphToFlatTransformer2(GraphConfig config) {
		this.config = config;
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	public Map<Vertex, Point2D> transform(Graph<Vertex, Unit<Edge>> graph) {
		Map<Vertex, Point2D> res = getExistingPositions(graph);
		return (res != null ? res : format(graph));
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	private Map<Vertex, Point2D> getExistingPositions(Graph<Vertex, Unit<Edge>> graph) {
		Map<Vertex, Point2D> ret = new HashMap<Vertex, Point2D>();

		Vertex start = null;
		Vertex finish = null;
		for (Vertex v : graph.getVertices()) {
			if (v instanceof VertexRoom) {
				Attribute a = ((VertexRoom) v).getAttribute();
				if (a == null) {
					ret.clear();
					return null;
				} else {
					ret.put(v, new Point(a.getX(), a.getY()));
				}
			}
			if (v instanceof VertexStart) {
				start = v;
			}
			if (v instanceof VertexFinish) {
				finish = v;
			}
		}
		if ((start == null) || (finish == null)) {
			throw new IllegalStateException("start or finish vertex not found");
		}
		ret.put(start, getPositionForSpecalVertex(graph, ret, start));
		ret.put(finish, getPositionForSpecalVertex(graph, ret, finish));

		return ret;
	}

	private Point2D getPositionForSpecalVertex(Graph<Vertex, Unit<Edge>> graph, Map<Vertex, Point2D> map, Vertex v) {
		if (!(v instanceof VertexSpecial)) {
			throw new IllegalStateException("getPositionForSpecalVertex() : argument is notspecial vertex");
		}
		Collection<Unit<Edge>> edges = ((v instanceof VertexStart) ? graph.getOutEdges(v) : graph.getInEdges(v));
		List<Point2D> points = new ArrayList<Point2D>();
		for (Unit<Edge> e : edges) {
			Pair<Vertex> p = graph.getEndpoints(e);
			Vertex vr = (p.getFirst().equals(v) ? p.getSecond() : p.getFirst());
			if (!(vr instanceof VertexSpecial)) {
				points.add(map.get(vr));
			}
		}
		double x = 0.0;
		double y = 0.0;
		if (!points.isEmpty()) {
			x = points.get(0).getX();
			y = points.get(0).getY();

			for (Point2D p : points) {
				if (v instanceof VertexStart) {
					x = ( x <= p.getX() ? x : p.getX());
					y = (y + p.getY() ) / 2;
//					ret.setLocation(((ret.getX() <= p.getX()) ? ret.getX() : p.getX()), ((ret.getY() + p.getY()) / 2));
				} else {
					x = ( x >= p.getX() ? x : p.getX());
					y = (y + p.getY() ) / 2;

//					ret.setLocation(((ret.getX() >= p.getX()) ? ret.getX() : p.getX()), ((ret.getY() + p.getY()) / 2));
				}
			}
//			ret.setLocation(ret.getX() + diff * (100 + config.getRoomWidth() / 2), ret.getY());
		}
		double k = (v instanceof VertexStart ? -1.0 : 1.0);
		return new Point2D.Double(x + k * (100 + config.getRoomWidth() * 2), y);
	}

	private Map<Vertex, Point2D> format(Graph<Vertex, Unit<Edge>> graph) {
		List<Vertex> starts = new ArrayList<Vertex>();
		for (Vertex it : graph.getVertices()) {
			if (graph.getInEdges(it).isEmpty()) {
				starts.add(it);
			}
		}
		if (starts.isEmpty()) {
			throw new IllegalArgumentException("graph haven't start's vertexes");
		}
		Map<Vertex, Point2D> ret = new HashMap<Vertex, Point2D>();
		int offsetY = 0;
		for (Vertex v : starts) {
			Map<Vertex, Point2D> map = calcVertexPositions(graph, v, new Point(0, 0), null);
			map = shiftX(graph, map);
			map = setY(map);
			moveVertexCoordinatesByY(map, offsetY);

			int[] arr = getGraphHeightAllocation(map);
			Arrays.sort(arr);
			offsetY += arr[arr.length - 1];

			for (Point2D it : map.values()) {
				it.setLocation(it.getX() * 200 + 100, it.getY() * 100 + 50);
			}

			ret.putAll(map);
		}
		return ret;
	}

	/**
	 * calculate dirty position for all graph's vertex
	 * @param graph - existing directed graph
	 * @param vertex - start vertex
	 * @param level - start level, must be 0
	 * @param map - must be null
	 * @return position for graph's vertex
	 */
	private Map<Vertex, Point2D> calcVertexPositions(Graph<Vertex, Unit<Edge>> graph, Vertex vertex, Point2D startPoint
			, Map<Vertex, Point2D> map) {
		if (map == null) {
			map = new HashMap<Vertex, Point2D>();
		}
		if (map.containsKey(vertex)) {
			return map;
		}
		map.put(vertex, startPoint);
		for (Unit<Edge> edge : graph.getOutEdges(vertex)) {
			Vertex v = graph.getOpposite(vertex, edge);
			map = calcVertexPositions(graph, v, new Point((int) (startPoint.getX() + 1), 0), map);
		}
		return map;
	}

	/**
	 * correct x positions for all graph's vertex
	 * @param graph - existing directed graph
	 * @param vertex - start vertex
	 * @param map - graph's vertex
	 * @param level - start level, must be 0
	 * @return correct position for graph's vertex
	 */
	private Map<Vertex, Point2D> shiftX(Graph<Vertex, Unit<Edge>> graph, Map<Vertex, Point2D> map) {
		int i = 0;
		boolean cycle;

		do {
			i++;
			cycle = false;
			for (Entry<Vertex, Point2D> it : map.entrySet()) {
				if (Math.round(it.getValue().getX()) == i) {
					cycle = true;
					int pos = (int) Math.round(it.getValue().getX());


					for (Unit<Edge> edge : graph.getInEdges(it.getKey())) {
						Vertex v = graph.getOpposite(it.getKey(), edge);
						int pos2 = (int) Math.round(map.get(v).getX());
						if ((pos <= pos2) && !isAccessibility(graph, it.getKey(), v, null)) {
							pos = pos2 + 1;
						}
					}

					if (Math.round(it.getValue().getX()) != pos) {
						it.getValue().setLocation(pos, it.getValue().getY());
					}
				}
			}
		} while (cycle);

		return map;
	}

	/**
	 * check loop for 2 vertex
	 * @param graph
	 * @param start
	 * @param finish
	 * @param use
	 * @return
	 */
	private boolean isAccessibility(Graph<Vertex, Unit<Edge>> graph, Vertex start, Vertex finish, Set<Vertex> use) {
		if (use == null) {
			use = new HashSet<Vertex>();
		}
		if (start.equals(finish)) {
			return true;
		}
		if (use.contains(start)) {
			return false;
		}
		use.add(start);

		for (Unit<Edge> it : graph.getOutEdges(start)) {
			Vertex v = graph.getOpposite(start, it);
			if (isAccessibility(graph, v, finish, use)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @param map
	 * @return
	 */
	private Map<Vertex, Point2D> setY(Map<Vertex, Point2D> map) {
		int[] yAllocation = getGraphHeightAllocation(map);

		int yMax = 0;
		for (int i : yAllocation) {
			if (yMax < i) {
				yMax = i;
			}
		}

		int[] pos = new int [yAllocation.length];

		Arrays.fill(pos, 0);
		for (Point2D it : map.values()) {
			it.setLocation(it.getX(), (int) (((float)pos[(int) Math.round(it.getX())] + 0.5) * yMax / yAllocation[(int) Math.round(it.getX())]) );
			pos[(int) Math.round(it.getX())]++;
		}
		return map;
	}

	/**
	 * calculate quantity vertexes for every X
	 * @param map
	 * @return
	 */
	private int[] getGraphHeightAllocation(Map<Vertex, Point2D> map) {
		int max = getGraphLength(map) + 1;
		int[] arr = new int[max];
		Arrays.fill(arr,  0);

		for (Point2D it : map.values()) {
			arr[(int) Math.round(it.getX())]++;
		}
		return arr;
	}

	/**
	 * find max vertex's X
	 * @param map
	 * @return
	 */
	private int getGraphLength(Map<Vertex, Point2D> map) {
		int max = 0;
		for (Point2D it : map.values()) {
			if (Math.round(it.getX()) > max) {
				max = (int) it.getX();
			}
		}
		return max;
	}

	/**
	 * offest
	 * @param map
	 * @param y
	 * @return
	 */
	private Map<Vertex, Point2D> moveVertexCoordinatesByY(Map<Vertex, Point2D> map, int y) {
		for (Point2D p : map.values()) {
			p.setLocation(p.getX(), p.getY() + y);
		}
		return map;
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================

}