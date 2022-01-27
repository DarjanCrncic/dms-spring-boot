package com.example.dms.services;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.DmsDocument;

public interface DocumentService extends CrudService<DmsDocument, DmsDocumentDTO, UUID>{

	DmsDocumentDTO createNewDocument(NewDocumentDTO newDocumentDTO);

	void uploadFile(UUID id, MultipartFile file);

	boolean checkIsDocumentValidForDownload(DmsDocument document);

	DmsDocumentDTO createNewVersion(UUID id);

	List<DmsDocumentDTO> getAllVersions(UUID id);

	DmsDocumentDTO updateDocument(UUID id, ModifyDocumentDTO modifyDocumentDTO, boolean patch);

	List<DmsDocumentDTO> getAllDocuments();

	ResponseEntity<byte[]> downloadContent(UUID id);

}
