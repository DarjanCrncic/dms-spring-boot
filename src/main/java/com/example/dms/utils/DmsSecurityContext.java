package com.example.dms.utils;

import com.example.dms.security.DmsUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class DmsSecurityContext {

	public static DmsUserDetails getAuthentication() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof DmsUserDetails) {
			return (DmsUserDetails) principal;
		}
		return null;
	}

	public static String getUsername() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof DmsUserDetails) {
			return ((DmsUserDetails) principal).getUsername();
		}
		return principal.toString();
	}
}
