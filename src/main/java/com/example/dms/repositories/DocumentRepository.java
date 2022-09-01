package com.example.dms.repositories;

import com.example.dms.domain.DmsDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<DmsDocument, UUID>, JpaSpecificationExecutor<DmsDocument>{

	List<DmsDocument> findAllByRootId(UUID id);

}
