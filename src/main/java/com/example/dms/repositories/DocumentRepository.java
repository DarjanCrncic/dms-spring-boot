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

	@Query(value="select * from dms_document where root_id in (select root_id from dms_document where id = ?1)", nativeQuery = true)
	List<DmsDocument> getAllVersions(UUID id);

}
