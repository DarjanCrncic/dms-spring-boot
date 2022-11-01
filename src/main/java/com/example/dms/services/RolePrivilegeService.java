package com.example.dms.services;

import com.example.dms.domain.security.DmsPrivilege;
import com.example.dms.domain.security.DmsRole;

import java.util.Collection;

public interface RolePrivilegeService {
	DmsRole findRoleByName(String name);

	Collection<DmsPrivilege> findPrivilegesByNames(Collection<String> names);
}
