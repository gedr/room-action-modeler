package ru.intech.ussd.modeler.entities;

import java.util.Objects;
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

	@Column(name = "name")
	private String name;

	@Transient
	private Integer hash;

	@Transient
	private UUID uuid = UUID.randomUUID();

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public Projection() {
	}

	public Projection(Projection other) {
		if (other != null) {
			this.setId(other.getId());
			this.setName(other.getName());
		}
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	@Override
	public String toString() {
		return "Projection [" + getId() + "] : " + getName();
	}

	@Override
	public int hashCode() {
		if (hash == null) {
			synchronized (Projection.class) {
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
		if (!Projection.class.equals(obj.getClass())) {
			return false;
		}
		Projection that = (Projection) obj;
		return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getName(), that.getName());
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================

}
