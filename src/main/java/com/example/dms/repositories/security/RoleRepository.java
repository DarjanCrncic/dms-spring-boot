package com.example.dms.repositories.security;

import com.example.dms.domain.security.DmsRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<DmsRole, Long>{
	Optional<DmsRole> findByName(String name);
}
