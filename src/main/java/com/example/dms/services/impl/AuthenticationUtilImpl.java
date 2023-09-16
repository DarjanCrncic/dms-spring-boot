package com.example.dms.services.impl;

import com.example.dms.security.DmsUserDetails;
import com.example.dms.services.AuthenticationUtil;
import com.example.dms.utils.UserDetailsUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationUtilImpl implements AuthenticationUtil {

	@Override
	public Authentication getCurrentAuthentication() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			throw new RuntimeException("Can't extract authentication, authentication is null.");
		}

		return auth;
	}

	@Override
	public DmsUserDetails getPrincipal() {
		Authentication authentication = getCurrentAuthentication();

		return UserDetailsUtil.extractDetailsFromPrincipal(authentication.getPrincipal(), null);
	}

	@Override
	public String getUserName() {
		return getPrincipal().getUsername();
	}
}
