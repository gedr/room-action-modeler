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
	private int hash;

	@Transient
	private boolean hashActive = false;

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
		if (!hashActive) {
			synchronized (Attribute.class) {
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
		if (!(obj instanceof Attribute)) {
			return false;
		}
		if (getId() == null) {
			return false;
		}
		Attribute other = (Attribute) obj;
		return Objects.equals(this.getId(), other.getId())
				&& (this.getX() == other.getX())
				&& (this.getY() == other.getY())
				&& (this.getColorAsNum() == other.getColorAsNum());
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

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	public Color getColor() {
		return new Color(colorAsNum);
	}

	public void setColorAsNum(Color color) {
		this.colorAsNum = color.getRGB();
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
