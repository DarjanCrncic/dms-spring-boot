package com.example.dms.services;

import com.example.dms.api.dtos.SortDTO;
import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.DmsDocument;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentService extends CrudService<DmsDocument, DmsDocumentDTO, UUID>{

	DmsDocumentDTO createDocument(NewDocumentDTO newDocumentDTO);

	DmsDocumentDTO createNewVersion(UUID id);

	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','VERSION') || hasAuthority('VERSION_PRIVILEGE')")
	DmsDocumentDTO createNewBranch(UUID id);

	List<DmsDocumentDTO> getAllVersions(UUID id);

	DmsDocumentDTO updateDocument(UUID id, ModifyDocumentDTO modifyDocumentDTO, boolean patch);

	List<DmsDocumentDTO> searchAll(Optional<String> search, Optional<SortDTO> sort);

	List<DmsDocumentDTO> copyDocuments(UUID folderId, List<UUID> documentIdList);

	List<DmsDocumentDTO> cutDocuments(UUID folderId, List<UUID> documents);
}
