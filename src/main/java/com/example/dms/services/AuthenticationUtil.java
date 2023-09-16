package com.example.dms.services;

import com.example.dms.security.DmsUserDetails;
import org.springframework.security.core.Authentication;

public interface AuthenticationUtil {
	Authentication getCurrentAuthentication();

	DmsUserDetails getPrincipal();

	String getUserName();
}
