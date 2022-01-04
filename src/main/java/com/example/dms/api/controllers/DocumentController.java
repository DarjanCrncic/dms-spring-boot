package com.example.dms.api.controllers;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.dms.api.dtos.document.DocumentDTO;
import com.example.dms.api.dtos.document.DocumentFileDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.api.mappers.DocumentMapper;
import com.example.dms.domain.Document;
import com.example.dms.services.DocumentService;
import com.example.dms.utils.exceptions.BadRequestException;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

	@Autowired
	DocumentMapper documentMapper;
	
	@Autowired
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

	@PostMapping("/{id}/upload")
	public DocumentFileDTO uploadDocumentContent(@PathVariable UUID id, @RequestBody MultipartFile file) {
		if (file == null)
			throw new BadRequestException("The file parameter in the request body is null.");
		documentService.uploadFile(id, file);
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/documents/" + id).path("/download")
				.toUriString();
		return new DocumentFileDTO(id, fileDownloadUri, file.getContentType(), file.getSize());
	}

	@GetMapping("/{id}/download")
	public ResponseEntity<byte[]> downloadDocumentContent(@PathVariable UUID id) {
		Document document = documentService.findById(id);
		documentService.checkIsDocumentValidForDownload(document);
		return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getOriginalFileName() + "\"")
                .contentType(MediaType.valueOf(document.getContentType()))
                .body(document.getContent());
	}
}
