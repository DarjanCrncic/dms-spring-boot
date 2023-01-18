package com.example.dms.api.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

import static com.example.dms.utils.Constants.MIN_LENGTH_2;
import static com.example.dms.utils.Constants.MIN_LENGTH_4;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewUserDTO {

	@NotBlank
	@Length(min = MIN_LENGTH_4, message = "Invalid username length, username must have at least " + MIN_LENGTH_4
			+ " characters.")
	private String username;

	@NotBlank
	@Length(min = MIN_LENGTH_4, message = "Invalid password length, password must have at least " + MIN_LENGTH_4
			+ " characters.")
	private String password;

	@NotBlank
	@Length(min = MIN_LENGTH_2, message = "Invalid first name length, first name must have at least " + MIN_LENGTH_2 + " characters.")
	private String firstName;

	@NotBlank
	@Length(min = MIN_LENGTH_2, message = "Invalid last name length, last name must have at least " + MIN_LENGTH_2 + " characters.")
	private String lastName;

	@Email
	@Column(unique = true)
	private String email;

	private String role;
	private List<String> privileges;

}