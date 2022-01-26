package com.example.dms.api.controllers;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.dms.api.dtos.document.DocumentDTO;
import com.example.dms.api.dtos.document.DocumentFileDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.services.DocumentService;
import com.example.dms.utils.exceptions.BadRequestException;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

	@Autowired
	DocumentService documentService;

	@PostMapping("/")
	@ResponseStatus(value = HttpStatus.CREATED)
	public DocumentDTO createNewDocument(@Valid @RequestBody NewDocumentDTO newDocumentDTO) {
		return documentService.createNewDocument(newDocumentDTO);
	}

	@GetMapping("/")
	public List<DocumentDTO> getAllDocuments() {
		return documentService.getAllDocuments();
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
		return documentService.downloadContent(id);
	}
	
	@PutMapping("/{id}")
	public DocumentDTO updateDocumentPut(@PathVariable UUID id, @RequestBody @Valid ModifyDocumentDTO modifyDocumentDTO) {
		return documentService.updateDocument(id, modifyDocumentDTO, false);
	}
	
	@PatchMapping("/{id}")
	public DocumentDTO updateDocumentPatch(@PathVariable UUID id, @RequestBody @Valid ModifyDocumentDTO modifyDocumentDTO) {
		return documentService.updateDocument(id, modifyDocumentDTO, true);
	}
	
	// TODO: DELETE MAPPINGS
}
