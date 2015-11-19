package ru.intech.ussd.modeler.graphobjects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import ru.intech.ussd.modeler.entities.EntryPoint;

public class EdgeStart implements Edge{
	// =================================================================================================================
	// Constants
	// =================================================================================================================

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private EntryPoint entryPoint;
	private String key = null;
	private String description = null;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public EdgeStart(EntryPoint ep) {
		setEntryPoint(ep);
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	public boolean isChanged() {
		return entryPoint == null ? true : !(StringUtils.equals(getKey(), entryPoint.getUserMessage())
				&& StringUtils.equals(getDescription(), entryPoint.getDescription()));
	}

	public void applyChanges() {
		if (!isChanged()) {
			return;
		}
		Validate.notBlank(key);
		if (entryPoint == null) {
			entryPoint = new EntryPoint();
			entryPoint.setActive(true);
		}
		entryPoint.setDescription(getDescription());
		entryPoint.setUserMessage(getKey());
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public EntryPoint getEntryPoint() {
		return entryPoint;
	}

	public void setEntryPoint(EntryPoint entryPoint) {
		this.entryPoint = entryPoint;
		if (entryPoint != null) {
			setKey(entryPoint.getUserMessage());
			setDescription(entryPoint.getDescription());
		}
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
