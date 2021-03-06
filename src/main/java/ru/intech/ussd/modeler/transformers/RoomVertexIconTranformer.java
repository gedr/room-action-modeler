package ru.intech.ussd.modeler.transformers;

import javax.swing.Icon;

import org.apache.commons.collections15.Transformer;

import ru.intech.ussd.modeler.config.GraphConfig;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.ui.VertexIcon;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class RoomVertexIconTranformer implements Transformer<Vertex, Icon> {
	// =================================================================================================================
	// Constants
	// =================================================================================================================

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private PickedState<Vertex> pickedState;
	private GraphConfig config;
	private double scale = 1.0;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public RoomVertexIconTranformer(PickedState<Vertex> pickedState, GraphConfig config) {
		this.pickedState = pickedState;
		this.config = config;
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	public Icon transform(Vertex vertex) {
		return new VertexIcon(vertex, pickedState == null ? false : pickedState.isPicked(vertex), config, scale);
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public PickedState<Vertex> getPickedState() {
		return pickedState;
	}

	public void setPickedState(PickedState<Vertex> pickedState) {
		this.pickedState = pickedState;
	}

	public GraphConfig getConfig() {
		return config;
	}

	public void setConfig(GraphConfig config) {
		this.config = config;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
