package com.example.dms.api.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.dms.api.dtos.document.DocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.api.mappers.DocumentMapper;
import com.example.dms.domain.Document;
import com.example.dms.services.DocumentService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/documents")
public class DocumentController {
	
	DocumentMapper documentMapper;
	DocumentService documentService;

	@PostMapping("/")
	@ResponseStatus(value = HttpStatus.CREATED)
	public DocumentDTO createNewDocument(@Valid @RequestBody NewDocumentDTO newDocumentDTO) {
		Document newDocument = documentService.createNewDocument(newDocumentDTO);
		return documentMapper.documentToDocumentDTO(newDocument);
	}
	
	@GetMapping("/")
	public List<DocumentDTO> getAllDocuments() {
		return documentMapper.documentListToDocumentDTOList(documentService.findAll());
	}
	
}
