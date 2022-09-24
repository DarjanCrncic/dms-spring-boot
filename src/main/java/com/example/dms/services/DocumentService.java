package com.example.dms.services;

import com.example.dms.api.dtos.SortDTO;
import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.DmsDocument;

import java.util.List;
import java.util.UUID;

public interface DocumentService extends CrudService<DmsDocument, DmsDocumentDTO, UUID>{

	DmsDocumentDTO createDocument(NewDocumentDTO newDocumentDTO);

	DmsDocumentDTO createNewVersion(UUID id);

	DmsDocumentDTO createNewBranch(UUID id);

	List<DmsDocumentDTO> getAllVersions(UUID id);

	DmsDocumentDTO updateDocument(UUID id, ModifyDocumentDTO modifyDocumentDTO, boolean patch);

	List<DmsDocumentDTO> searchAll(String search, SortDTO sort);

	List<DmsDocumentDTO> copyDocuments(UUID folderId, List<UUID> documentIdList);

	List<DmsDocumentDTO> cutDocuments(UUID folderId, List<UUID> documents);
}
