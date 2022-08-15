package com.example.dms.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.dms.domain.DmsDocument;

@Repository
public interface DocumentRepository extends JpaRepository<DmsDocument, UUID>, JpaSpecificationExecutor<DmsDocument>{

	List<DmsDocument> findAllByRootId(UUID id);

}
