package ru.intech.ussd.modeler.transformers;

import org.apache.commons.collections15.Transformer;

import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.EdgeAction;
import ru.intech.ussd.modeler.graphobjects.EdgeStart;
import ru.intech.ussd.modeler.util.Unit;

public class EdgeLabelTransformer implements Transformer<Unit<Edge>, String> {
	// =================================================================================================================
	// Constants
	// =================================================================================================================

	// =================================================================================================================
	// Fields
	// =================================================================================================================

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public EdgeLabelTransformer() {

	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	public String transform(Unit<Edge> input) {
		if (input == null) {
			return null;
		}
		if (input.getValue() instanceof EdgeAction) {
			return String.valueOf(((EdgeAction) input.getValue()).getKey());
		}
		if (input.getValue() instanceof EdgeStart) {
			return ((EdgeStart) input.getValue()).getKey();
		}
		return null;
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
