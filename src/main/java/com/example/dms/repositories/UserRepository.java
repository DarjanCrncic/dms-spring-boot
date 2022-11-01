package com.example.dms.repositories;

import com.example.dms.domain.DmsUser;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<DmsUser, UUID>{

	Optional<DmsUser> findByUsername(String username);

	Optional<DmsUser> findByEmail(String email);

	List<DmsUser> findAll(Specification<DmsUser> parse, Sort toSort);

	List<DmsUser> findAllByIdIn(Collection<UUID> ids);

	@Modifying
	@Query(nativeQuery = true, value = "UPDATE ACL_SID SET sid = ?2 WHERE sid = ?1")
	void updateUsername(String oldUsername, String newUsername);

	@Modifying
	@Query(nativeQuery = true, value = "DELETE FROM ACL_ENTRY WHERE sid = (SELECT id FROM ACL_SID WHERE sid = ?1)")
	void removeAclEntries(String username);

	List<DmsUser> findAllByUsernameIn(Set<String> usernames);

	@Query("SELECT u FROM DmsUser u INNER JOIN u.roles r WHERE r.name = ?1")
	List<DmsUser> findByRoleName(String roleName);
}
