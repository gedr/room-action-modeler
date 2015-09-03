package ru.intech.ussd.modeler.graphobjects;

import org.apache.commons.lang3.StringUtils;

import ru.intech.ussd.modeler.entities.Action;

public class EdgeAction implements Edge {
	// =================================================================================================================
	// Constants
	// =================================================================================================================

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private Action action;
	private String description;
	private char key;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public EdgeAction(Action action) {
		setAction(action);
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	public boolean isChanged() {
		return action == null ? true: ((getKey() == action.getKey())
				&& StringUtils.equals(getDescription(), action.getDescription()));
	}

	public void applyChanges() {
		if (!isChanged()) {
			return;
		}
		if (action == null) {
			action = new Action();
		}
		action.setKey(getKey());
		action.setDescription(getDescription());
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public char getKey() {
		return key;
	}

	public void setKey(char key) {
		this.key = key;
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
