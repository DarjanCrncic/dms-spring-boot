package com.example.dms.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dms.domain.DmsUser;

@Repository
public interface UserRepository extends JpaRepository<DmsUser, UUID>{

	Optional<DmsUser> findByUsername(String username);

	Optional<DmsUser> findByEmail(String email);

}
