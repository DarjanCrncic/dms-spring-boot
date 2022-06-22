package com.example.dms.utils;

public enum Privileges {
	DELETE_PRIVILEGE,		// can delete all types of objects
	PERMISSION_PRIVILEGE,	// can change permissions on documents and folders
	VERSION_PRIVILEGE,		// can version documents
	CREATE_PRIVILEGE,		// can create documents and folders
	WRITE_PRIVILEGE,		// can update documents and folders
	READ_PRIVILEGE,			// can read documents and see folders
}
