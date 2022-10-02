package com.example.dms.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Privileges {
	READ_PRIVILEGE,				// can read documents and see folders
	WRITE_PRIVILEGE,			// can update documents and folders
	CREATE_PRIVILEGE,			// can create documents and folders
	VERSION_PRIVILEGE,			// can version documents
	DELETE_PRIVILEGE,			// can delete all types of objects
	ADMINISTRATION_PRIVILEGE;	// can change permissions on documents and folders

	public static List<String> getAsString() {
		return Arrays.stream(values()).map(Enum::name).collect(Collectors.toList());
	}
}
