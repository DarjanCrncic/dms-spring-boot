package com.example.dms.api.controllers;

import com.example.dms.api.dtos.folder.DmsFolderDTO;
import com.example.dms.api.dtos.folder.FolderTreeDTO;
import com.example.dms.api.dtos.folder.NewFolderDTO;
import com.example.dms.api.dtos.folder.UpdateFolderDTO;
import com.example.dms.services.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/folders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
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
	public DmsFolderDTO getFolderById(@PathVariable Integer id) {
		return folderService.findById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public DmsFolderDTO createNewFolder(@RequestBody @Valid NewFolderDTO newFolderDTO) {
		return folderService.createFolder(newFolderDTO);
	}

	@PutMapping("/{id}")
	public DmsFolderDTO updateFolder(@PathVariable Integer id, @RequestBody @Valid UpdateFolderDTO updateFolderDTO) {
		return folderService.updateFolder(id, updateFolderDTO.getName());
	}

	@DeleteMapping("/{id}")
	public void deleteFolderById(@PathVariable Integer id) {
		folderService.deleteFolder(id);
	}

	@PostMapping("/move/{id}")
	public DmsFolderDTO moveFilesToFolder(@PathVariable Integer id, @RequestBody List<Integer> documentIdList) {
		return folderService.moveFilesToFolder(id, documentIdList);
	}
}
