package ru.intech.ussd.modeler.entities;

import java.awt.Color;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(schema = "test", name = "attributes")
public class Attribute implements java.io.Serializable {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final long serialVersionUID = 2293360107058594708L;

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

	@Column(name = "color")
	private int colorAsNum;

	@Transient
	private Integer hash;

	@Transient
	private UUID uuid = UUID.randomUUID();

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public Attribute() {
	}

	public Attribute(Attribute other) {
		if (other != null) {
			setId(other.getId());
			setX(other.getX());
			setY(other.getY());
			setColorAsNum(other.getColorAsNum());
		}
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	@Override
	public String toString() {
		return "Attribute : {id=" + getId() + ", x=" + getX() + ", y=" + getY() + ", color=" + getColorAsNum() + "}";
	}

	@Override
	public int hashCode() {
		if (hash == null) {
			synchronized (EntryPoint.class) {
				if (hash == null) {
					hash = (id != null ? id : uuid.hashCode());
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
		if (!Attribute.class.equals(obj.getClass())) {
			return false;
		}
		Attribute that = (Attribute) obj;
		return Objects.equals(this.getId(), that.getId())
				&& (this.getX() == that.getX())
				&& (this.getY() == that.getY())
				&& (this.getColorAsNum() == that.getColorAsNum());
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

	public int getColorAsNum() {
		return colorAsNum;
	}

	public void setColorAsNum(int colorAsNum) {
		this.colorAsNum = colorAsNum;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	public Color getColor() {
		return new Color(colorAsNum);
	}

	public void setColor(Color color) {
		this.colorAsNum = color.getRGB();
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
