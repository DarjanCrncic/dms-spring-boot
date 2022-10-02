package com.example.dms.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Roles {
	ROLE_ADMIN,	// superuser who can do everything
	ROLE_USER; 	// basic user, fine tune with privileges

	public static List<String> getAsString() {
		return Arrays.stream(values()).map(Enum::name).collect(Collectors.toList());
	}
}
