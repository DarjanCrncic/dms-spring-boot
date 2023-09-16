package com.example.dms.repositories;

import com.example.dms.domain.DmsDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<DmsDocument, Integer>, JpaSpecificationExecutor<DmsDocument>{

	List<DmsDocument> findAllByRootId(Integer id);

	Collection<Object> findByParentFolderId(Integer id);
}
