package ru.intech.ussd.modeler.graphobjects;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import ru.intech.ussd.modeler.entities.Projection;
import ru.intech.ussd.modeler.entities.Room;
import ru.intech.ussd.modeler.entities.RoomPosition;

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
	private RoomPosition position;
	private Set<Projection> projections = new HashSet<Projection>();

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
				&& StringUtils.equals(getFunction(), room.getFunction()) && (isFinish() == room.isFinish())
				&& CollectionUtils.isEqualCollection(projections, room.getProjections())
				&& Objects.equals(position, room.getPosition()));
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
		if (getPosition() != null) {
			if (room.getPosition() == null) {
				room.setPosition(getPosition());
			} else {
				room.getPosition().setX(getPosition().getX());
				room.getPosition().setY(getPosition().getY());
			}
		}

		if (!CollectionUtils.isEqualCollection(projections, room.getProjections())) {
			room.setProjections(projections);
		}
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
			setPosition(new RoomPosition(room.getPosition()));
			if (!room.getProjections().isEmpty()) {
				projections.addAll(room.getProjections());
			}
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

	public Set<Projection> getProjections() {
		return projections;
	}

	public void setProjections(Set<Projection> projections) {
		this.projections = projections;
	}

	public RoomPosition getPosition() {
		return position;
	}

	public void setPosition(RoomPosition position) {
		this.position = position;
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	public boolean addProjection(Projection projection) {
		return projections.add(projection);
	}

	public boolean removeProjection(Projection projection) {
		return projections.remove(projection);
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
