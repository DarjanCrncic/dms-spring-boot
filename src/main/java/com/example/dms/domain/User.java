package com.example.dms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=true)
@Entity
public class User extends BaseEntity{
	
	@NotBlank
	@Length(min = 5, max = 32)
	@Column(unique=true)
	private String username;
	
	@NotBlank
	@Length(min = 5, max = 32)
	private String password;
	
	@NotBlank
	@Length(min = 5, max = 32)
	private String firstName;
	
	@NotBlank
	@Length(min = 5, max = 32)
	private String lastName;
	
	@Email
	@Column(unique=true)
	private String email;

}
