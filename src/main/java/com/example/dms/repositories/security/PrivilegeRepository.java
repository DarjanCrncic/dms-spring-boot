package com.example.dms.repositories.security;

import com.example.dms.domain.security.DmsPrivilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface PrivilegeRepository extends JpaRepository<DmsPrivilege, Long>{
	Optional<DmsPrivilege> findByName(String name);

	Collection<DmsPrivilege> findAllByNameIn(Collection<String> names);
}
