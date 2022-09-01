package com.example.dms.repositories;

import com.example.dms.domain.DmsContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContentRepository extends JpaRepository<DmsContent, UUID>{

}
