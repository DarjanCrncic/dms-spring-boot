package com.example.dms.utils;

public enum Roles {
	ROLE_ADMIN,	// superuser who can do everything
	ROLE_CREATOR, // user who can create documents and version documents
	ROLE_EDITOR, // user who can edit documents but cannot create or version them
	ROLE_USER // user who can only read documents
}
