package ru.intech.ussd.modeler.transformers;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.util.Unit;
import edu.uci.ics.jung.graph.Graph;

/**
 * project graph to flat area
 * @author egafarov
 *
 */
public class GraphToFlatTranformer implements Transformer<Graph<Vertex, Unit<Edge>>, Map<Vertex, Point2D>>{
	// =================================================================================================================
	// Constants
	// =================================================================================================================

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private Vertex startVertex = null;
	private Map<Vertex, Point2D> map = new HashMap<Vertex, Point2D>();

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public GraphToFlatTranformer() {

	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	public Map<Vertex, Point2D> transform(Graph<Vertex, Unit<Edge>> graph) {
		for (Vertex it : graph.getVertices()) {
			if (graph.getInEdges(it).isEmpty()) {
				startVertex = it;
				break;
			}
		}
		Map<Vertex, Point2D> map = calcVertexPositions(graph, startVertex, new Point(0, 0));
		map = shiftX(graph);
		map = setY(map);

		for (Point2D it : map.values()) {
			it.setLocation(it.getX() * 200 + 100, it.getY() * 100 + 50);
		}

		return map;
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	/**
	 * calculate position for all graph's vertex
	 * @param graph - existing directed graph
	 * @param vertex - start vertex
	 * @param level - start level, must be 0
	 * @param map - must be null
	 * @return position for graph's vertex
	 */
	private Map<Vertex, Point2D> calcVertexPositions(Graph<Vertex, Unit<Edge>> graph, Vertex vertex, Point2D startPoint) {
		if (map.containsKey(vertex)) {
			return map;
		}

		map.put(vertex, startPoint);
		for (Unit<Edge> edge : graph.getOutEdges(vertex)) {
			Vertex v = graph.getOpposite(vertex, edge);
			map = calcVertexPositions(graph, v, new Point((int) (startPoint.getX() + 1), 0));
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
	private Map<Vertex, Point2D> shiftX(Graph<Vertex, Unit<Edge>> graph) {
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

	private int[] getGraphHeightAllocation(Map<Vertex, Point2D> map) {
		int max = getGraphLength(map) + 1;
		int[] arr = new int[max];
		Arrays.fill(arr,  0);

		for (Point2D it : map.values()) {
			arr[(int) Math.round(it.getX())]++;
		}
		return arr;
	}

	private int getGraphLength(Map<Vertex, Point2D> map) {
		int max = 0;
		for (Point2D it : map.values()) {
			if (Math.round(it.getX()) > max) {
				max = (int) it.getX();
			}
		}
		return max;
	}


	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
