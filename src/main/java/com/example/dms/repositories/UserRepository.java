package com.example.dms.repositories;

import com.example.dms.domain.DmsUser;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<DmsUser, UUID>{

	Optional<DmsUser> findByUsername(String username);

	Optional<DmsUser> findByEmail(String email);

	List<DmsUser> findAll(Specification<DmsUser> parse, Sort toSort);
}
