package ru.intech.ussd.modeler.graphobjects;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import ru.intech.ussd.modeler.entities.EntryPoint;

public class EdgeStart implements Edge{
	// =================================================================================================================
	// Constants
	// =================================================================================================================

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private EntryPoint src;
	private EntryPoint edt;

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
		return !Objects.equals(src, edt);
	}

	public void applyChanges() {
		if (!isChanged()) {
			return;
		}
		Validate.notBlank(getKey());
		if (src == null) {
			src = new EntryPoint(edt);
		} else {
			src.setActive(isActive());
			src.setDescription(getDescription());
			src.setUserMessage(getKey());
		}
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public EntryPoint getEntryPoint() {
		return src;
	}

	public void setEntryPoint(EntryPoint entryPoint) {
		this.src = entryPoint;
		edt = new EntryPoint(src);
	}

	public Integer getId() {
		return src.getId();
	}

	public String getKey() {
		return edt.getUserMessage();
	}

	public void setKey(String key) {
		edt.setUserMessage(key);
	}

	public String getDescription() {
		return edt.getDescription();
	}

	public void setDescription(String description) {
		edt.setDescription(description);
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
