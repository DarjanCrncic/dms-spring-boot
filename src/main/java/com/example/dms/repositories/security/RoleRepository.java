package com.example.dms.repositories.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dms.domain.security.DmsRole;

public interface RoleRepository extends JpaRepository<DmsRole, Long>{
	Optional<DmsRole> findByName(String name);
}
