package com.example.dms.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.example.dms.domain.security.DmsPrivilege;
import com.example.dms.domain.security.DmsRole;
import com.example.dms.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
@Entity
public class DmsUser extends BaseEntity {

	@NotBlank
	@NonNull
	@Length(min = Constants.MINLENGTH, message = "Ivalid username length, username must have atleast "
			+ Constants.MINLENGTH + " characters.")
	@Column(unique = true)
	private String username;

	@NotBlank
	@NonNull
	@Length(min = Constants.MINLENGTH, message = "Ivalid password length, password must have atleast "
			+ Constants.MINLENGTH + " characters.")
	private String password;

	@NotBlank
	@NonNull
	@Length(min = 2, message = "Ivalid first name length, first name must have atleast 2 characters.")
	private String firstName;

	@NotBlank
	@NonNull
	@Length(min = 2, message = "Ivalid last name length, last name must have atleast 2 characters.")
	private String lastName;

	@Email
	@NonNull
	@Column(unique = true)
	private String email;

	@ManyToMany(mappedBy = "members")
	@ToString.Exclude
	@JsonIgnore // TODO: change this to use JsonView
	@Default
	private Set<DmsGroup> groups = new HashSet<>();

	@OneToMany(mappedBy = "creator")
	@ToString.Exclude
	@JsonIgnore
	@Default
	private List<DmsDocument> documents = new ArrayList<>();
	
	@OneToMany(mappedBy = "user")
	@ToString.Exclude
	@JsonIgnore
	@Default
	private List<DmsDocumentColumnPreference> documentColumnPreferences = new ArrayList<>();

	@ManyToMany
	@Default
	@JoinTable(name = "users_roles",
		joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), 
		inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private List<DmsRole> roles = new ArrayList<>();
	
	@ManyToMany
    @JoinTable(
        name = "users_privileges", 
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), 
        inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    private List<DmsPrivilege> privileges;
	
	@Default
	boolean enabled = true;
}
