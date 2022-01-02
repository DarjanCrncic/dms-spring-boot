package com.example.dms.services;

import java.util.UUID;

import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.Document;

public interface DocumentService extends CrudService<Document, UUID>{

	Document createNewDocument(NewDocumentDTO newDocumentDTO);

}
