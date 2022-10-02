package com.example.dms.services;

import com.example.dms.domain.security.DmsPrivilege;
import com.example.dms.domain.security.DmsRole;

import java.util.Collection;
import java.util.List;

public interface RolePrivilegeService {
	DmsRole findRoleByName(String name);

	List<DmsPrivilege> findPrivilegesByNames(Collection<String> names);
}
