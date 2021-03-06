package ru.intech.ussd.modeler.entities;
// default package
// Generated 07.08.2015 14:31:53 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections15.CollectionUtils;

/**
 * RoomHeaders generated by hbm2java
 */
@Entity
@Table(schema = "graph", name = "rooms")
public class Room implements java.io.Serializable {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final long serialVersionUID = 4693511109430912022L;

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
	@SequenceGenerator(name = "seq", sequenceName = "graph.rooms_id_seq", allocationSize = 1)
	@Column(name = "id")
	private Integer id;

	@Column(name = "dscr")
	private String description;

	@Column(name = "func")
	private String function;

	@Column(name = "finish")
	private boolean finish;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "projections_rooms", schema = "graph",
	joinColumns = { @JoinColumn(name = "room_id", nullable = false, updatable = false) },
	inverseJoinColumns = { @JoinColumn(name = "projection_id", nullable = false, updatable = false) })
	private Set<Projection> projections = new HashSet<Projection>();

	@OneToOne(fetch = FetchType.EAGER)
	@PrimaryKeyJoinColumn
	private Attribute attribute;

	@Transient
	private Integer hash;

	@Transient
	private UUID uuid = UUID.randomUUID();

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public Room() {
	}

	public Room(Room other) {
		if (other != null) {
			setId(other.getId());
			setDescription(other.getDescription());
			setFunction(other.getFunction());
			setFinish(other.isFinish());
			setAttribute(new Attribute(other.getAttribute()));
			setProjections(other.getProjections());
		}
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	@Override
	public String toString() {
		return "Room [" + getId() + "] : " + getDescription() + " --> " + getFunction();
	}


	@Override
	public int hashCode() {
		if (hash == null) {
			synchronized (Room.class) {
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
		if (!Room.class.equals(obj.getClass())) {
			return false;
		}
		Room that = (Room) obj;
		boolean idcmpr = Objects.equals(this.getId(), that.getId());
		if (idcmpr && this.getId() == null) {
			idcmpr = this.getUuid().equals(that.getUuid());
		}

		return idcmpr && Objects.equals(this.getDescription(), that.getDescription())
				&& Objects.equals(this.getFunction(), that.getFunction())
				&& (this.isFinish() == that.isFinish())
				&& Objects.equals(this.getAttribute(), that.getAttribute())
				&& CollectionUtils.isEqualCollection(this.getProjections(), that.getProjections());
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

	public String getDescription() {
		return this.description;
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

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public UUID getUuid() {
		return this.uuid;
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
