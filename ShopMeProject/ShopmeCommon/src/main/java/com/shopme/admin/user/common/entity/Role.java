package com.shopme.admin.user.common.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.IntPredicate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
public class Role extends IdBasedEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Column(length = 100, nullable = false, unique = true)
	private String name;
	
	@Column(length = 250, nullable = false)
	private String description;

	public Role() {
	}

	public Role(Integer id) {
		this.id = id;
	}

	public Role(String name) {
		this.name = name;
	}	
	
	public Role(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Role role = (Role) o;
		return Objects.equals(getName(), role.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

	@Override
	public String toString() {
		return this.name;
	}
}