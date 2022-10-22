package com.example.dms.security;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LoginResponse {

	private String username;
	private String token;
	private long expiresAt;
	private String firstName;
	private String lastName;
	private List<String> privileges;
	private List<String> roles;
	private List<String> groupIdentifiers;
}
