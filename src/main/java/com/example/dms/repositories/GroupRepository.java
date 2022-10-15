package com.example.dms.repositories;

import com.example.dms.domain.DmsGroup;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<DmsGroup, UUID>{

	Optional<DmsGroup> findByGroupName(String groupName);

	List<DmsGroup> findAll(Specification<DmsGroup> parse, Sort toSort);
}
