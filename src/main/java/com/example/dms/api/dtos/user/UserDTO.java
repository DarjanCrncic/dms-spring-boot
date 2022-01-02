package com.example.dms.api.dtos.user;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class UserDTO {
	
	@NotBlank
	@Length(min = 5, max = 32)
	@Column(unique=true)
	private String username;
	
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
