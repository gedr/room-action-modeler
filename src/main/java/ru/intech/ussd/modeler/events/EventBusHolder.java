package ru.intech.ussd.modeler.events;

import com.google.common.eventbus.EventBus;


public class EventBusHolder {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final EventBus eb = new EventBus("Main");

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
	public static EventBus getEventBus() {
		return eb;
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}