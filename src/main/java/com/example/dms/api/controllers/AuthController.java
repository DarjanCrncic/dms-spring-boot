package com.example.dms.api.controllers;

import com.example.dms.domain.DmsUser;
import com.example.dms.security.DmsUserDetails;
import com.example.dms.security.JwtUtils;
import com.example.dms.security.LoginRequest;
import com.example.dms.security.LoginResponse;
import com.example.dms.utils.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;

	@Value("${dms.jwt.expiration}")
	private int jwtExpirationMs;

	@PostMapping("/login")
	public LoginResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = null;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		} catch (BadCredentialsException e) {
			throw new BadRequestException("Invalid username or password");
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		DmsUserDetails userDetails = (DmsUserDetails) authentication.getPrincipal();
		DmsUser user = userDetails.getUser();

		return new LoginResponse(userDetails.getUsername(), jwt, System.currentTimeMillis() + jwtExpirationMs,
				user.getFirstName(), user.getLastName(), userDetails.getPrivileges(), userDetails.getRoles(),
				userDetails.getGroupIdentifiers());
	}
}