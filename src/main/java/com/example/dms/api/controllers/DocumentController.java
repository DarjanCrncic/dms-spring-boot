package com.example.dms.api.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.dms.api.dtos.document.DocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.api.mappers.DocumentMapper;
import com.example.dms.services.DocumentService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class DocumentController {
	
	DocumentMapper documentMapper;
	DocumentService documentService;

	@PostMapping("/api/v1/document")
	public DocumentDTO createNewDocument(@RequestBody NewDocumentDTO newDocumentDTO) {
		return documentMapper.documentToDocumentDTO(documentService.createNewDocument(newDocumentDTO));
	}
	
}
