package com.example.dms.domain;

import com.example.dms.domain.security.DmsPrivilege;
import com.example.dms.domain.security.DmsRole;
import com.example.dms.utils.Roles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.Builder.Default;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.dms.utils.Constants.MIN_LENGTH_2;
import static com.example.dms.utils.Constants.MIN_LENGTH_4;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class DmsUser extends BaseEntity {

	@NotBlank
	@NonNull
	@Length(min = MIN_LENGTH_4, message = "Invalid username length, username must have at least " + MIN_LENGTH_4 + " characters.")
	@Column(unique = true)
	private String username;

	@NotBlank
	@NonNull
	@Length(min = MIN_LENGTH_4, message = "Invalid password length, password must have at least " + MIN_LENGTH_4 + " characters.")
	private String password;

	@NotBlank
	@NonNull
	@Length(min = MIN_LENGTH_2, message = "Invalid first name length, first name must have at least " + MIN_LENGTH_2 + "characters.")
	private String firstName;

	@NotBlank
	@NonNull
	@Length(min = MIN_LENGTH_2, message = "Invalid last name length, last name must have " + MIN_LENGTH_2 + " characters.")
	private String lastName;

	@Email
	@NonNull
	@Column(unique = true)
	private String email;

	@ManyToMany(mappedBy = "members", fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JsonIgnore // TODO: change this to use JsonView
	@Default
	private Set<DmsGroup> groups = new HashSet<>();

	@OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
	@JsonIgnore
	@Default
	private List<DmsDocument> documents = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	@JsonIgnore
	@Default
	private List<DmsDocumentColumnPreference> documentColumnPreferences = new ArrayList<>();

	@OneToMany(mappedBy = "recipient", orphanRemoval = true)
	@Fetch(FetchMode.SUBSELECT)
	@Default
	private Set<DmsNotification> notifications = new HashSet<>();

	@ManyToMany
	@Fetch(FetchMode.SUBSELECT)
	@Default
	@JoinTable(name = "users_roles",
			joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
			uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "role_id" }, name = "user_role_constraint"))
	private Set<DmsRole> roles = new HashSet<>();

	@ManyToMany
	@Fetch(FetchMode.SUBSELECT)
	@Default
	@JoinTable(
			name = "users_privileges",
			joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"),
			uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "privilege_id" }, name = "user_privilege_constraint"))
	private Set<DmsPrivilege> privileges = new HashSet<>();

	@Default
	boolean enabled = true;

	public boolean isAdminRole() {
		return roles.stream().map(DmsRole::getName)
				.collect(Collectors.toList())
				.contains(Roles.ROLE_ADMIN.name());
	}
}
