package com.example.dms.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dms.domain.DmsGroup;

public interface GroupRepository extends JpaRepository<DmsGroup, UUID>{

	Optional<DmsGroup> findByGroupName(String groupName);

}
