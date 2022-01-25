package com.example.dms.services;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.example.dms.api.dtos.document.DocumentDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.DmsDocument;

public interface DocumentService extends CrudService<DmsDocument, DocumentDTO, UUID>{

	DocumentDTO createNewDocument(NewDocumentDTO newDocumentDTO);

	void uploadFile(UUID id, MultipartFile file);

	boolean checkIsDocumentValidForDownload(DmsDocument document);

	DocumentDTO createNewVersion(UUID id);

	List<DocumentDTO> getAllVersions(UUID id);

	DocumentDTO updateDocument(UUID id, ModifyDocumentDTO modifyDocumentDTO, boolean patch);

	List<DocumentDTO> getAllDocuments();

	ResponseEntity<byte[]> downloadContent(UUID id);

}
