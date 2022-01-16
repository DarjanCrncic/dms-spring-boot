package com.example.dms.services;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.DmsDocument;

public interface DocumentService extends CrudService<DmsDocument, UUID>{

	DmsDocument createNewDocument(NewDocumentDTO newDocumentDTO);

	void uploadFile(UUID id, MultipartFile file);

	boolean checkIsDocumentValidForDownload(DmsDocument document);

}
