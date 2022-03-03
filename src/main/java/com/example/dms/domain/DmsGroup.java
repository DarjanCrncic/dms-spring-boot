package com.example.dms.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
@Builder
@Entity
public class DmsGroup extends BaseEntity {

	@NotEmpty
	@Length(min = 5, max = 32)
	@Column(unique = true)
	private String groupName;
	
	@Default
	private String description = null;

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "user_groups", joinColumns = { @JoinColumn(name = "group_id") }, 
			inverseJoinColumns = { @JoinColumn(name = "user_id") })
	@Default
	private Set<DmsUser> members = new HashSet<>();
}
