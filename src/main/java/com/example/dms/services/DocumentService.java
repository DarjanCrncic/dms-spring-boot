package com.example.dms.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.example.dms.api.dtos.SortDTO;
import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.DmsDocument;

public interface DocumentService extends CrudService<DmsDocument, DmsDocumentDTO, UUID>{

	DmsDocumentDTO createDocument(NewDocumentDTO newDocumentDTO);

	void uploadFile(UUID id, MultipartFile file);

	boolean checkIsDocumentValidForDownload(DmsDocument document);

	DmsDocumentDTO createNewVersion(UUID id);

	List<DmsDocumentDTO> getAllVersions(UUID id);

	DmsDocumentDTO updateDocument(UUID id, ModifyDocumentDTO modifyDocumentDTO, boolean patch);

	ResponseEntity<byte[]> downloadContent(UUID id);

	List<DmsDocumentDTO> searchAll(Optional<String> search, Optional<SortDTO> sort);

	List<DmsDocumentDTO> copyDocuments(UUID folderId, List<UUID> documentIdList);

}
