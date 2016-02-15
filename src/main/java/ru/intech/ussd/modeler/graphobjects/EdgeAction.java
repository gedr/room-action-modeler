package ru.intech.ussd.modeler.graphobjects;

import java.util.Objects;

import ru.intech.ussd.modeler.entities.Action;

public class EdgeAction implements Edge {
	// =================================================================================================================
	// Constants
	// =================================================================================================================

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private Action src;
	private Action edt;

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
		return !Objects.equals(src, edt);
	}

	public void applyChanges() {
		if (!isChanged()) {
			return;
		}
		if (src == null) {
			src = new Action(edt);
		} else {
			src.setKey(getKey());
			src.setDescription(getDescription());
			src.setActive(isActive());
		}
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public Action getAction() {
		return src;
	}

	public void setAction(Action action) {
		this.src = action;
		edt = new Action(action);
	}

	public String getDescription() {
		return edt.getDescription();
	}

	public void setDescription(String description) {
		edt.setDescription(description);
	}

	public Integer getId() {
		return src.getId();
	}

	public char getKey() {
		return edt.getKey();
	}

	public void setKey(char key) {
		edt.setKey(key);
	}

	public boolean isActive() {
		return edt.isActive();
	}

	public void setActive(boolean active) {
		edt.setActive(active);
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
