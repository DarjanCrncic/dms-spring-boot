package com.example.dms.repositories;

import com.example.dms.domain.DmsGroup;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<DmsGroup, UUID>{

	Optional<DmsGroup> findByGroupName(String groupName);

	Optional<DmsGroup> findByIdentifier(String identifier);

	boolean existsByIdentifier(String identifier);

	List<DmsGroup> findAll(Specification<DmsGroup> parse, Sort toSort);

	@Modifying
	@Query(nativeQuery = true, value = "UPDATE ACL_SID SET sid = ?2 WHERE sid = ?1")
	void updateIdentifier(String oldIdentifier, String newIdentifier);


	@Modifying
	@Query(nativeQuery = true, value = "DELETE FROM ACL_ENTRY WHERE sid = (SELECT id FROM ACL_SID WHERE sid = ?1)")
	void removeAclEntries(String username);
}
