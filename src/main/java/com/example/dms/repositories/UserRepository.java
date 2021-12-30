package com.example.dms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dms.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

}
