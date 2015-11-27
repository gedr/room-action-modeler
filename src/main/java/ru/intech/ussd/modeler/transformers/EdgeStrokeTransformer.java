package ru.intech.ussd.modeler.transformers;

import java.awt.BasicStroke;
import java.awt.Stroke;

import org.apache.commons.collections15.Transformer;

import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.EdgeAction;
import ru.intech.ussd.modeler.graphobjects.EdgeFinish;
import ru.intech.ussd.modeler.graphobjects.EdgeStart;
import ru.intech.ussd.modeler.util.Unit;

public class EdgeStrokeTransformer implements Transformer<Unit<Edge>, Stroke> {

	// =================================================================================================================
	// Constants
	// =================================================================================================================
	static final Stroke DOTED_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {10.0f}, 0.0f);
	static final Stroke SOLID_STROKE = new BasicStroke(1.0f);

//  float dash[] = {10.0f};
//
//  Transformer<Edge, Stroke> edgeStrokeTransformer = new Transformer<Edge, Stroke>() {
//      public Stroke transform(Edge s) {
//          return edgeStroke;
//      }
//  };

	// =================================================================================================================
	// Fields
	// =================================================================================================================

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public EdgeStrokeTransformer() {

	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	public Stroke transform(Unit<Edge> input) {
		Stroke s = SOLID_STROKE;
		if ((input != null) && (input.getValue() != null) && !(input.getValue() instanceof EdgeFinish)) {
			Edge e = input.getValue();
			if ( ((e instanceof EdgeStart) && !((EdgeStart) e).isActive())
				|| ((e instanceof EdgeAction) && !((EdgeAction) e).isActive()) ) {
				s = DOTED_STROKE;
			}
		}
		return s;
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================

	// =================================================================================================================
	// Methods
	// =================================================================================================================

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
