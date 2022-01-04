package com.example.dms.services;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.Document;

public interface DocumentService extends CrudService<Document, UUID>{

	Document createNewDocument(NewDocumentDTO newDocumentDTO);

	void uploadFile(UUID id, MultipartFile file);

	boolean checkIsDocumentValidForDownload(Document document);

}
