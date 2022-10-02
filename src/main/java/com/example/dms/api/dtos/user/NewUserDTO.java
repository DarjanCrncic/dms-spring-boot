package com.example.dms.api.dtos.user;

import com.example.dms.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewUserDTO {

	@NotBlank
	@Length(min = Constants.MINLENGTH, message = "Ivalid username length, username must have atleast " + Constants.MINLENGTH
			+ " characters.")
	private String username;

	@NotBlank
	@Length(min = Constants.MINLENGTH, message = "Ivalid password length, password must have atleast " + Constants.MINLENGTH
			+ " characters.")
	private String password;

	@NotBlank
	@Length(min = 2, message = "Ivalid first name length, first name must have atleast 2 characters.")
	private String firstName;

	@NotBlank
	@Length(min = 2, message = "Ivalid last name length, last name must have atleast 2 characters.")
	private String lastName;

	@Email
	@Column(unique = true)
	private String email;

	private String role;
	private List<String> privileges;

}