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
	private Room room;
	private Room editRoom;
//	private String description;
//	private String function;
//	private boolean finish;
//	private Attribute attribute;
//	private Set<Projection> projections = new HashSet<Projection>();

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public VertexRoom(Room room) {
		this.room = room;
		editRoom = new Room(room);
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	@Override
	public String toString() {
		return "VertexRoom < " + room + " >";
	}

	@Override
	public int hashCode() {
		return room.hashCode();
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
		return room.equals(((VertexRoom)obj).getRoom());
	};

	public boolean isChanged() {
		return !Objects.equals(room, editRoom);
//		return room == null ? true : !(StringUtils.equals(getDescription(), room.getDescription())
//				&& StringUtils.equals(getFunction(), room.getFunction()) && (isFinish() == room.isFinish())
//				&& CollectionUtils.isEqualCollection(projections, room.getProjections())
//				&& Objects.equals(attribute, room.getAttribute()));
	}

	public void applyChanges() {
		if (!isChanged()) {
			return;
		}
		Validate.notBlank(editRoom.getFunction());
		if (room == null) {
			room = new Room();
		}
		room.setFinish(isFinish());
		room.setDescription(getDescription());
		room.setFunction(getFunction());
		room.setAttribute(getAttribute());
		room.setProjections(getProjections());
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public Room getRoom() {
		return room;
	}

	public String getDescription() {
		return editRoom.getDescription();
	}

	public void setDescription(String description) {
		editRoom.setDescription(description);
	}

	public String getFunction() {
		return editRoom.getFunction();
	}

	public void setFunction(String function) {
		editRoom.setFunction(function);
	}

	public boolean isFinish() {
		return editRoom.isFinish();
	}

	public void setFinish(boolean finish) {
		editRoom.setFinish(finish);
	}

	public Set<Projection> getProjections() {
		return editRoom.getProjections();
	}

	public void setProjections(Set<Projection> projections) {
		editRoom.setProjections(projections);
	}

	public Attribute getAttribute() {
		return editRoom.getAttribute();
	}

	public void setAttribute(Attribute attribute) {
		editRoom.setAttribute(attribute);
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	public boolean addProjection(Projection projection) {
		return editRoom.addProjection(projection);
	}

	public boolean removeProjection(Projection projection) {
		return editRoom.removeProjection(projection);
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
