package com.example.dms.api.controllers;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.dms.api.dtos.folder.DmsFolderDTO;
import com.example.dms.api.dtos.folder.FolderTreeDTO;
import com.example.dms.api.dtos.folder.NewFolderDTO;
import com.example.dms.api.dtos.folder.UpdateFolderDTO;
import com.example.dms.security.DmsUserDetails;
import com.example.dms.services.FolderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/folders")
@RequiredArgsConstructor
public class FolderController {

	private final FolderService folderService;

	@GetMapping
	public List<DmsFolderDTO> getAllFolders() {
		return folderService.findAll();
	}

	@GetMapping("/tree")
	public List<FolderTreeDTO> getFolderTreeDTO() {
		return folderService.getFolderTreeNew();
	}

	@GetMapping("/{id}")
	public DmsFolderDTO getFolderById(@PathVariable UUID id) {
		return folderService.findById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public DmsFolderDTO createNewFolder(@RequestBody @Valid NewFolderDTO newFolderDTO,
			@AuthenticationPrincipal DmsUserDetails userDetails) {
		return folderService.createFolder(newFolderDTO.getName(), userDetails.getUsername(), newFolderDTO.getParentFolderId());
	}

	@PutMapping("/{id}")
	public DmsFolderDTO updateFolder(@PathVariable UUID id, @RequestBody @Valid UpdateFolderDTO updateFolderDTO) {
		return folderService.updateFolder(id, updateFolderDTO.getName());
	}

	@DeleteMapping("/{id}")
	public void deleteFolderById(@PathVariable UUID id) {
		folderService.deleteFolder(id);
	}

	@PostMapping("/move/{id}")
	public DmsFolderDTO moveFilesToFolder(@PathVariable UUID id, @RequestBody List<UUID> documentIdList) {
		return folderService.moveFilesToFolder(id, documentIdList);
	}
}
