package com.example.dms.services.impl;

import com.example.dms.domain.security.DmsPrivilege;
import com.example.dms.domain.security.DmsRole;
import com.example.dms.repositories.security.PrivilegeRepository;
import com.example.dms.repositories.security.RoleRepository;
import com.example.dms.services.RolePrivilegeService;
import com.example.dms.utils.exceptions.DmsNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RolePrivilegeServiceImpl implements RolePrivilegeService {

	private final RoleRepository roleRepository;
	private final PrivilegeRepository privilegeRepository;

	@Override
	public DmsRole findRoleByName(String name) {
		return roleRepository.findByName(name)
				.orElseThrow(() -> new DmsNotFoundException("Role with given name not found."));
	}

	@Override
	public List<DmsPrivilege> findPrivilegesByNames(Collection<String> names) {
		return privilegeRepository.findAllByNameIn(names);
	}
}
