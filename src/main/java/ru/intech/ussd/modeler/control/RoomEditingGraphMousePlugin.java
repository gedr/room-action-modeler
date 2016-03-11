package ru.intech.ussd.modeler.control;

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingGraphMousePlugin;

public class RoomEditingGraphMousePlugin<V, E> extends EditingGraphMousePlugin<V, E> {
	// =================================================================================================================
	// Constants
	// =================================================================================================================

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private CreateEdgeControl<V> vetoableNewEdge;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public RoomEditingGraphMousePlugin(Factory<V> vertexFactory, Factory<E> edgeFactory) {
		super(MouseEvent.BUTTON3_MASK, vertexFactory, edgeFactory);
    }

	public RoomEditingGraphMousePlugin(Factory<V> vertexFactory, Factory<E> edgeFactory, CreateEdgeControl<V> vne) {
		super(MouseEvent.BUTTON3_MASK, vertexFactory, edgeFactory);
		this.vetoableNewEdge = vne;
    }

    public RoomEditingGraphMousePlugin(int modifiers, Factory<V> vertexFactory, Factory<E> edgeFactory) {
    	super(modifiers, vertexFactory, edgeFactory);
    }

    public RoomEditingGraphMousePlugin(int modifiers, Factory<V> vertexFactory, Factory<E> edgeFactory, CreateEdgeControl<V> vne) {
    	super(modifiers, vertexFactory, edgeFactory);
    	this.vetoableNewEdge = vne;
    }

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
    @Override
	public void mousePressed(MouseEvent e) {
		if (checkModifiers(e)) {
			@SuppressWarnings("unchecked")
			final VisualizationViewer<V, E> vv = (VisualizationViewer<V, E>) e.getSource();
			final Point2D p = e.getPoint();
			GraphElementAccessor<V, E> pickSupport = vv.getPickSupport();
			if (pickSupport != null) {
				Graph<V, E> graph = vv.getModel().getGraphLayout().getGraph();
				edgeIsDirected = EdgeType.DIRECTED;

				final V vertex = pickSupport.getVertex(vv.getModel().getGraphLayout(), p.getX(), p.getY());
				if (vertex != null) { // get ready to make an edge
					startVertex = vertex;
					down = e.getPoint();
					transformEdgeShape(down, down);
					vv.addPostRenderPaintable(edgePaintable);
					transformArrowShape(down, down); //e.getPoint());
					vv.addPostRenderPaintable(arrowPaintable);
				} else { // make a new vertex
					V newVertex = vertexFactory.create();
					Layout<V, E> layout = vv.getModel().getGraphLayout();
					graph.addVertex(newVertex);
					layout.setLocation(newVertex,
							vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint()));
				}
			}
			vv.repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
        if (checkModifiers(e)) {
            @SuppressWarnings("unchecked")
			final VisualizationViewer<V,E> vv = (VisualizationViewer<V,E>)e.getSource();
            final Point2D p = e.getPoint();
            Layout<V,E> layout = vv.getModel().getGraphLayout();
            GraphElementAccessor<V,E> pickSupport = vv.getPickSupport();
            if (pickSupport != null) {
                final V vertex = pickSupport.getVertex(layout, p.getX(), p.getY());
                if ((vertex != null) && (startVertex != null)
                		&& ((vetoableNewEdge == null) || vetoableNewEdge.veto(EdgeType.DIRECTED, startVertex, vertex))) {
                    Graph<V,E> graph = vv.getGraphLayout().getGraph();
					graph.addEdge(edgeFactory.create(), startVertex, vertex, edgeIsDirected);
					vv.repaint();
				}
            }
            startVertex = null;
            down = null;
            edgeIsDirected = EdgeType.UNDIRECTED;
            vv.removePostRenderPaintable(edgePaintable);
            vv.removePostRenderPaintable(arrowPaintable);
        }
    }

    @Override
    public boolean checkModifiers(MouseEvent e) {
        return (modifiers - e.getModifiers()) == 0;
    }

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public CreateEdgeControl<V> getVetoableNewEdge() {
		return vetoableNewEdge;
	}

	public void setVetoableNewEdge(CreateEdgeControl<V> vne) {
		this.vetoableNewEdge = vne;
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	private void transformEdgeShape(Point2D down, Point2D out) {
        float x1 = (float) down.getX();
        float y1 = (float) down.getY();
        float x2 = (float) out.getX();
        float y2 = (float) out.getY();

        AffineTransform xform = AffineTransform.getTranslateInstance(x1, y1);

        float dx = x2-x1;
        float dy = y2-y1;
        float thetaRadians = (float) Math.atan2(dy, dx);
        xform.rotate(thetaRadians);
        float dist = (float) Math.sqrt(dx*dx + dy*dy);
        xform.scale(dist / rawEdge.getBounds().getWidth(), 1.0);
        edgeShape = xform.createTransformedShape(rawEdge);
    }

    private void transformArrowShape(Point2D down, Point2D out) {
        float x1 = (float) down.getX();
        float y1 = (float) down.getY();
        float x2 = (float) out.getX();
        float y2 = (float) out.getY();

        AffineTransform xform = AffineTransform.getTranslateInstance(x2, y2);

        float dx = x2-x1;
        float dy = y2-y1;
        float thetaRadians = (float) Math.atan2(dy, dx);
        xform.rotate(thetaRadians);
        arrowShape = xform.createTransformedShape(rawArrowShape);
    }

    // =============================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
