package com.example.dms.services;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.DmsDocument;

public interface DocumentService extends CrudService<DmsDocument, UUID>{

	DmsDocument createNewDocument(NewDocumentDTO newDocumentDTO);

	void uploadFile(UUID id, MultipartFile file);

	boolean checkIsDocumentValidForDownload(DmsDocument document);

	DmsDocument createNewVersion(UUID id);

	List<DmsDocument> getAllVersions(UUID id);

	DmsDocument updateDocument(UUID id, ModifyDocumentDTO modifyDocumentDTO, boolean patch);

}
