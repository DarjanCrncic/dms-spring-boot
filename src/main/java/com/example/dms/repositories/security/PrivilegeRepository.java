package com.example.dms.repositories.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dms.domain.security.DmsPrivilege;

public interface PrivilegeRepository extends JpaRepository<DmsPrivilege, Long>{
	Optional<DmsPrivilege> findByName(String name);
}
