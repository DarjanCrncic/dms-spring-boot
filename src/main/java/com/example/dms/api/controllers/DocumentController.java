package com.example.dms.api.controllers;

import com.example.dms.api.dtos.SortDTO;
import com.example.dms.api.dtos.document.*;
import com.example.dms.services.ContentService;
import com.example.dms.services.DocumentService;
import com.example.dms.utils.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

	private final DocumentService documentService;
	private final ContentService contentService;

	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	public DmsDocumentDTO createNewDocument(@Valid @RequestBody NewDocumentDTO newDocumentDTO) {
		return documentService.createDocument(newDocumentDTO);
	}

	@GetMapping
	public List<DmsDocumentDTO> getAllDocuments(@RequestParam(required = false) String search, SortDTO sort) {
		return documentService.searchAll(search, sort);
	}

	@PostMapping("/batch")
	@ResponseStatus(value = HttpStatus.CREATED)
	public List<DmsDocumentDTO> createNewDocumentInBatch(@Valid @RequestBody List<NewDocumentDTO> newDocumentDTOList) {
		return newDocumentDTOList.stream().map(documentService::createDocument).collect(Collectors.toList());
	}

	@PostMapping("/upload/{id}")
	public DocumentFileDTO uploadDocumentContent(@PathVariable Integer id, @RequestBody MultipartFile file) {
		if (file == null) throw new BadRequestException("The file parameter in the request body is null.");
		contentService.uploadFile(id, file);
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/documents/download"
				+ id).toUriString();
		return new DocumentFileDTO(id, fileDownloadUri, file.getContentType(), file.getSize(),
				file.getOriginalFilename());
	}

	@GetMapping("/{id}")
	public DmsDocumentDTO getDocumentById(@PathVariable Integer id) {
		return documentService.findById(id);
	}

	@GetMapping("/download/{id}")
	public ResponseEntity<byte[]> downloadDocumentContent(@PathVariable Integer id) {
		return contentService.downloadContent(id);
	}

	@PutMapping("/{id}")
	public DmsDocumentDTO updateDocumentPut(@PathVariable Integer id,
											@RequestBody @Valid ModifyDocumentDTO modifyDocumentDTO) {
		return documentService.updateDocument(id, modifyDocumentDTO, false);
	}

	@PatchMapping("/{id}")
	public DmsDocumentDTO updateDocumentPatch(@PathVariable Integer id,
											  @RequestBody @Valid ModifyDocumentDTO modifyDocumentDTO) {
		return documentService.updateDocument(id, modifyDocumentDTO, true);
	}

	@DeleteMapping("/{id}")
	public void deleteDocumentById(@PathVariable Integer id) {
		documentService.deleteById(id);
	}

	@DeleteMapping
	public void deleteMultipleDocuments(@RequestParam List<Integer> ids) {
		ids.forEach(documentService::deleteById);
	}

	@PostMapping("/copy")
	public List<DmsDocumentDTO> copyDocuments(@RequestBody CopyDocumentsDTO dto) {
		return documentService.copyDocuments(dto.getFolderId(), dto.getDocuments());
	}

	@PostMapping("/cut")
	public List<DmsDocumentDTO> cutDocuments(@RequestBody CopyDocumentsDTO dto) {
		return documentService.cutDocuments(dto.getFolderId(), dto.getDocuments());
	}

	@PostMapping("/version/{id}")
	public DmsDocumentDTO versionDocument(@PathVariable Integer id) {
		return documentService.createNewVersion(id);
	}

	@PostMapping("/branch/{id}")
	public DmsDocumentDTO branchDocument(@PathVariable Integer id) {
		return documentService.createNewBranch(id);
	}

	@GetMapping("/versions/{id}")
	public List<DmsDocumentDTO> getAllVersions(@PathVariable Integer id) {
		return documentService.getAllVersions(id);
	}
}
