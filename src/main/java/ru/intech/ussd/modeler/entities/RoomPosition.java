package ru.intech.ussd.modeler.entities;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(schema = "test", name = "room_positions")
public class RoomPosition implements java.io.Serializable {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final long serialVersionUID = 7401581910413338336L;

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	@Id
	@Column(name = "room_id")
	private Integer id;

	@Column(name = "xpos")
	private int x;

	@Column(name = "ypos")
	private int y;

	@Transient
	private int hash;

	@Transient
	private boolean hashActive = false;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public RoomPosition() {
	}

	public RoomPosition(RoomPosition other) {
		if (other != null) {
			setId(other.getId());
			setX(other.getX());
			setY(other.getY());
		}
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	@Override
	public String toString() {
		return "RoomPosition [" + getId() + "] : (" + getX() + ", " + getY() + ")";
	}


	@Override
	public int hashCode() {
		if (!hashActive) {
			synchronized (RoomPosition.class) {
				if (!hashActive) {
					hashActive = true;
					hash = (getId() == null ? UUID.randomUUID().hashCode() : getId());
				}
			}
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RoomPosition)) {
			return false;
		}
		if (getId() == null) {
			return false;
		}
		RoomPosition other = (RoomPosition) obj;
		return (Objects.equals(id, other.id) && (x == other.x) && (y == other.y));
	};

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}