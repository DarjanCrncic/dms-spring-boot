package com.example.dms.api.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.dms.api.dtos.SortDTO;
import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.DocumentFileDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.security.DmsUserDetails;
import com.example.dms.services.DocumentService;
import com.example.dms.utils.exceptions.BadRequestException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Log4j2
public class DocumentController {

	private final DocumentService documentService;

	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	public DmsDocumentDTO createNewDocument(@Valid @RequestBody NewDocumentDTO newDocumentDTO,
			@AuthenticationPrincipal DmsUserDetails userDetails) {
		newDocumentDTO.setUsername(userDetails.getUsername());
		return documentService.createDocument(newDocumentDTO);
	}

	@GetMapping
	public List<DmsDocumentDTO> getAllDocuments(@RequestParam Optional<String> search, Optional<SortDTO> sort) {
		log.debug("sort data: {}", sort.toString());
		if (search.isPresent()) {
			log.debug("search: {}", search.get());
			return documentService.searchAll(search.get(), sort);
		}
		return documentService.getAllDocuments(sort);
	}

	@PostMapping("/batch")
	@ResponseStatus(value = HttpStatus.CREATED)
	public List<DmsDocumentDTO> createNewDocumentInBatch(@Valid @RequestBody List<NewDocumentDTO> newDocumentDTOList,
			@AuthenticationPrincipal DmsUserDetails userDetails) {
		return newDocumentDTOList.stream().map(documentDTO -> {
			documentDTO.setUsername(userDetails.getUsername());
			return documentService.createDocument(documentDTO);
		}).collect(Collectors.toList());
	}

	@PostMapping("/upload/{id}")
	public DocumentFileDTO uploadDocumentContent(@PathVariable UUID id, @RequestBody MultipartFile file) {
		if (file == null)
			throw new BadRequestException("The file parameter in the request body is null.");
		documentService.uploadFile(id, file);
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/api/v1/documents/download" + id).toUriString();
		return new DocumentFileDTO(id, fileDownloadUri, file.getContentType(), file.getSize(),
				file.getOriginalFilename());
	}

	@GetMapping("/{id}")
	public DmsDocumentDTO getDocumentById(@PathVariable UUID id) {
		return documentService.findById(id);
	}

	@GetMapping("/download/{id}")
	public ResponseEntity<byte[]> downloadDocumentContent(@PathVariable UUID id) {
		return documentService.downloadContent(id);
	}

	@PutMapping("/{id}")
	public DmsDocumentDTO updateDocumentPut(@PathVariable UUID id,
			@RequestBody @Valid ModifyDocumentDTO modifyDocumentDTO) {
		return documentService.updateDocument(id, modifyDocumentDTO, false);
	}

	@PatchMapping("/{id}")
	public DmsDocumentDTO updateDocumentPatch(@PathVariable UUID id,
			@RequestBody @Valid ModifyDocumentDTO modifyDocumentDTO) {
		return documentService.updateDocument(id, modifyDocumentDTO, true);
	}

	@DeleteMapping("/{id}")
	public void deleteDocumentById(@PathVariable UUID id) {
		documentService.deleteById(id);
	}

	@DeleteMapping
	public void deleteMultipleDocuments(@RequestParam List<UUID> ids) {
		documentService.deleteInBatch(ids);
	}

}
