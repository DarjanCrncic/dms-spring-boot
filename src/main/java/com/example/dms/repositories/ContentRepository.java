package com.example.dms.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dms.domain.DmsContent;

@Repository
public interface ContentRepository extends JpaRepository<DmsContent, UUID>{

}
