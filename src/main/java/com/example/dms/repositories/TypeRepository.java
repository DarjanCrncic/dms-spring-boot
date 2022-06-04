package com.example.dms.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dms.domain.DmsType;

public interface TypeRepository extends JpaRepository<DmsType, UUID> {
 
	Optional<DmsType> findByTypeName(String typeName);

	boolean existsByTypeName(String typeName);
}
