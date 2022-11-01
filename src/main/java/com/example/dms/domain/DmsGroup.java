package com.example.dms.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class DmsGroup extends BaseEntity {

	@NotEmpty
	@Length(min = 2, max = 32)
	@Column(unique = true)
	private String groupName;

	@NotEmpty
	@Length(min = 2, max = 32)
	@Column(unique = true)
	private String identifier;

	@Default
	private String description = null;

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "users_groups", joinColumns = { @JoinColumn(name = "group_id") },
			inverseJoinColumns = { @JoinColumn(name = "user_id") })
	@Default
	private Set<DmsUser> members = new HashSet<>();
}
