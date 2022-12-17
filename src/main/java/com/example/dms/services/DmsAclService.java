package com.example.dms.services;

import com.example.dms.api.dtos.administration.GrantDTO;
import com.example.dms.domain.security.AclAllowedClass;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface DmsAclService {

	<T extends AclAllowedClass> void revokeRightsOnObject(T object, Sid sid, List<Permission> permissions);

	<T> void removeEntriesOnDelete(T object);

	<T extends AclAllowedClass> void grantRightsOnObject(T object, Sid sid, Collection<Permission> permissions);

	<T extends AclAllowedClass> void grantRightsOnObject(T object, String username,
			Collection<Permission> permissions);

	<T extends AclAllowedClass> boolean hasRight(T object, String username, Collection<Permission> permissions);

	<T extends AclAllowedClass> void revokeRightsOnObject(T object, String username, List<Permission> permissions);

	<T extends AclAllowedClass> List<GrantDTO> getRights(T object);

	<T extends AclAllowedClass> void copyRightsToAnotherEntity(T original, T copy);

	<T extends AclAllowedClass> Set<String> getRecipients(T object);

	<T extends AclAllowedClass> Set<String> getRecipients(T object, String filterPermission);
}
