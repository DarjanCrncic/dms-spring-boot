package com.example.dms.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

	private String username;
	private String token;
	private long expiresAt;
	private String firstName;
	private String lastName;
}
