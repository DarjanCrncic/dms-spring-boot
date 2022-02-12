package com.example.dms.security.configuration.acl;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.dms.utils.Roles;

public class DmsAclPermissionEvaluator extends AclPermissionEvaluator implements PermissionEvaluator{

	public DmsAclPermissionEvaluator(AclService aclService) {
		super(aclService);
	}

	@Override
	public boolean hasPermission(Authentication authentication, Object domainObject, Object permission) {
		GrantedAuthority adminAuthority = new SimpleGrantedAuthority(Roles.ROLE_ADMIN.name());
		if (authentication.getAuthorities().contains(adminAuthority)) {
			return true;
		}
		return super.hasPermission(authentication, domainObject, permission);
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
			Object permission) {
		GrantedAuthority adminAuthority = new SimpleGrantedAuthority(Roles.ROLE_ADMIN.name());
		if (authentication.getAuthorities().contains(adminAuthority)) {
			return true;
		}
		return super.hasPermission(authentication, targetId, targetType, permission);
	}
}
