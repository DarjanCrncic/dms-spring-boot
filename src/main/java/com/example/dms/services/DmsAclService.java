package com.example.dms.services;

import java.util.Collection;
import java.util.List;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import com.example.dms.domain.security.AclAllowedClass;

public interface DmsAclService {

	<T extends AclAllowedClass> void revokeRightsOnObject(T object, Sid sid, List<Permission> permissions);

	<T> void removeEntriesOnDelete(T object);

	<T extends AclAllowedClass> void grantRightsOnObject(T object, Sid sid, Collection<Permission> permissions);

	<T extends AclAllowedClass> void grantRightsOnObject(T object, String username,
			Collection<Permission> permissions);

	<T extends AclAllowedClass> boolean hasRight(T object, String username, Collection<Permission> permissions);

}
