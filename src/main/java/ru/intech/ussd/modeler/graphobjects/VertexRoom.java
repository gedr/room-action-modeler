package ru.intech.ussd.modeler.graphobjects;

import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import ru.intech.ussd.modeler.entities.Attribute;
import ru.intech.ussd.modeler.entities.Projection;
import ru.intech.ussd.modeler.entities.Room;

public class VertexRoom implements Vertex {
	// =================================================================================================================
	// Constants
	// =================================================================================================================

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private Room src;
	private Room edt;

	// =================================================================================================================
	// Constructors
	// =========================================================s========================================================
	public VertexRoom(Room room) {
		this.src = room;
		edt = new Room(room);
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	@Override
	public String toString() {
		return "VertexRoom < " + src + " >";
	}

	@Override
	public int hashCode() {
		return src.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof VertexRoom)) {
			return false;
		}
		VertexRoom other = (VertexRoom) obj;
		return Objects.equals(this.getRoom(),  other.getRoom());
	};

	public boolean isChanged() {
		return !Objects.equals(src, edt);
	}

	public void applyChanges() {
		if (!isChanged()) {
			return;
		}
		Validate.notBlank(edt.getFunction());
		if (src == null) {
			src = new Room(edt);
		} else {
			src.setFinish(isFinish());
			src.setDescription(getDescription());
			src.setFunction(getFunction());
			src.setAttribute(getAttribute());
			src.setProjections(getProjections());
		}
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public Room getRoom() {
		return src;
	}

	public String getDescription() {
		return edt.getDescription();
	}

	public void setDescription(String description) {
		edt.setDescription(description);
	}

	public String getFunction() {
		return edt.getFunction();
	}

	public void setFunction(String function) {
		edt.setFunction(function);
	}

	public boolean isFinish() {
		return edt.isFinish();
	}

	public void setFinish(boolean finish) {
		edt.setFinish(finish);
	}

	public Set<Projection> getProjections() {
		return edt.getProjections();
	}

	public void setProjections(Set<Projection> projections) {
		edt.setProjections(projections);
	}

	public Attribute getAttribute() {
		return edt.getAttribute();
	}

	public void setAttribute(Attribute attribute) {
		edt.setAttribute(attribute);
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	public boolean addProjection(Projection projection) {
		return edt.addProjection(projection);
	}

	public boolean removeProjection(Projection projection) {
		return edt.removeProjection(projection);
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
