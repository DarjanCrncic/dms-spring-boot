package com.example.dms.api.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dms.domain.DmsFolder;
import com.example.dms.services.FolderService;

@RestController
@RequestMapping("/api/v1/folders")
public class FolderController {

	FolderService folderService;

	public FolderController(FolderService folderService) {
		super();
		this.folderService = folderService;
	};
	
	@GetMapping("/")
	public List<DmsFolder> getAllFolders(@RequestParam Optional<String> path) {
		if (path.isPresent())
			return folderService.findByPath(path.get()).getSubfolders();
		return folderService.findAll();
	}
	
}
