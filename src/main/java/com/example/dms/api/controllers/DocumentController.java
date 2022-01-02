package com.example.dms.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.dms.api.dtos.document.DocumentDTO;
import com.example.dms.api.mappers.DocumentMapper;

@RestController
public class DocumentController {
	
	@Autowired
	DocumentMapper documentMapper;

	@PostMapping("/api/v1/document")
	public DocumentDTO createNewDocument(@RequestBody NewDocumentDTO) {
		
	}
	
}
