package com.example.dms.security.configuration.acl;

import com.example.dms.utils.Roles;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

public class DmsAclPermissionEvaluator extends AclPermissionEvaluator implements PermissionEvaluator{

	public DmsAclPermissionEvaluator(AclService aclService) {
		super(aclService);
	}

	@Override
	public boolean hasPermission(Authentication authentication, Object domainObject, Object permission) {
		if (checkAuthoritiesForAdminRole(authentication.getAuthorities())) return true;
		return super.hasPermission(authentication, domainObject, permission);
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		if (checkAuthoritiesForAdminRole(authentication.getAuthorities())) return true;
		return super.hasPermission(authentication, targetId, targetType, permission);
	}
	
	private boolean checkAuthoritiesForAdminRole(Collection<? extends GrantedAuthority> authorities) {
		boolean retVal = false;
		for (GrantedAuthority authority : authorities) {
			if (authority.getAuthority().equals(Roles.ROLE_ADMIN.name())) retVal = true;
		}
		return retVal;
	}
	
	public boolean hasPermission(Collection<Object> targetDomainIds, String targetType, Object permission, Authentication authentication) {
		for (Object targetDomainId : targetDomainIds) {
			if (!hasPermission(authentication, (Serializable) targetDomainId, targetType, permission))
				return false;
		}
		return true;
	}
	
}
