package ru.intech.ussd.modeler.entities;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(schema = "test", name = "projections")
public class Projection implements java.io.Serializable {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final long serialVersionUID = -3224385258993720532L;

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
	@SequenceGenerator(name = "seq", sequenceName = "test.layers_id_seq", allocationSize = 1)
	@Column(name = "id")
	private Integer id;

	@Column(name = "srv")
	private String service;

	@Column(name = "name")
	private String name;

	@Transient
	private int hash;

	@Transient
	private boolean hashActive = false;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public Projection() {
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	@Override
	public String toString() {
		return "Layer [" + getId() + "] : " + getService() + " --> " + getName();
	}


	@Override
	public int hashCode() {
		if (!hashActive) {
			synchronized (Projection.class) {
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
		if (!(obj instanceof Projection)) {
			return false;
		}
		if (getId() == null) {
			return false;
		}
		return getId().equals(((Projection) obj).getId());
	};

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getService() {
		return this.service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================

}
