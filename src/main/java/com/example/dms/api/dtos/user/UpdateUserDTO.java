package com.example.dms.api.dtos.user;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.example.dms.utils.Constants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {
	
	@NotBlank
	@Length(min = Constants.MINLENGTH, message = "Ivalid username length, username must have atleast " + Constants.MINLENGTH
			+ " characters.")
	@Column(unique = true)
	private String username;

	@NotBlank
	@Length(min = 2, message = "Ivalid first name length, first name must have atleast 2 characters.")
	private String firstName;

	@NotBlank
	@Length(min = 2, message = "Ivalid last name length, last name must have atleast 2 characters.")
	private String lastName;

	@Email
	@Column(unique = true)
	private String email;
	
}
