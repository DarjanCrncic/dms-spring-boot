package com.example.dms.security.configuration.acl;

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

public class CustomBasePermission extends BasePermission {

	public static final Permission VERSION = new CustomBasePermission(1 << 5, 'V');

	protected CustomBasePermission(int mask) {
		super(mask);
	}

	protected CustomBasePermission(int mask, char code) {
		super(mask, code);
	}
}
