package com.example.dms.services;

import java.util.List;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import com.example.dms.domain.security.AclAllowedClass;

public interface DmsAclService {

	<T extends AclAllowedClass> void grantCreatorRights(T object, String username);

	<T extends AclAllowedClass> void revokeRightsOnObject(T object, Sid sid, List<Permission> permissions);

	<T extends AclAllowedClass> void grantRightsOnObject(T object, Sid sid, List<Permission> permissions);

	<T> void removeEntriesOnDelete(T object);

}
