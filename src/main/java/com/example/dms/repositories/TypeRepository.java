package com.example.dms.repositories;

import com.example.dms.domain.DmsType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeRepository extends JpaRepository<DmsType, Integer> {
 
	Optional<DmsType> findByTypeName(String typeName);

	boolean existsByTypeName(String typeName);
}
