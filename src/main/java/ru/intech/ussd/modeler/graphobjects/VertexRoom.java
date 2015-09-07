package ru.intech.ussd.modeler.graphobjects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import ru.intech.ussd.modeler.entities.Room;

public class VertexRoom implements Vertex {
	// =================================================================================================================
	// Constants
	// =================================================================================================================

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private Room room;
	private String description;
	private String function;
	private boolean finish;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public VertexRoom(Room room) {
		setRoom(room);
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
		return room == null ? true : !(StringUtils.equals(getDescription(), room.getDescription())
				&& StringUtils.equals(getFunction(), room.getFunction()) && (isFinish() == room.isFinish()));
	}

	public void applyChanges() {
		if (!isChanged()) {
			return;
		}
		Validate.notBlank(function);
		if (room == null) {
			room = new Room();
		}
		room.setFinish(isFinish());
		room.setDescription(getDescription());
		room.setFunction(getFunction());
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
		if (room != null) {
			setDescription(room.getDescription());
			setFunction(room.getFunction());
			setFinish(room.isFinish());
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
