package com.example.dms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dms.domain.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>{

}
