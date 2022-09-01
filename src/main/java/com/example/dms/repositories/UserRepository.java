package com.example.dms.repositories;

import com.example.dms.domain.DmsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<DmsUser, UUID>{

	Optional<DmsUser> findByUsername(String username);

	Optional<DmsUser> findByEmail(String email);

}
