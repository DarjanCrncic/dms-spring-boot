package com.example.dms.api.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dms.api.dtos.administration.GrantDTO;
import com.example.dms.services.AdministrationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/administration")
@RequiredArgsConstructor
public class AdministrationController {
	
	private final AdministrationService administrationService;
	
	@PostMapping("/documents/grant/{id}")
	public List<GrantDTO> grantPermissionsDocuments(@RequestBody List<GrantDTO> grantDTOs, @PathVariable UUID id) {
		administrationService.grantRightsForDocument(grantDTOs, id);
		return grantDTOs;
	}
	
	@PostMapping("/folders/grant/{id}")
	public List<GrantDTO> grantPermissionsFolders(@RequestBody List<GrantDTO> grantDTOs, @PathVariable UUID id) {
		administrationService.grantRightsForFolder(grantDTOs, id);
		return grantDTOs;
	}
	
	@GetMapping("/documents/{id}")
	public List<GrantDTO> getPermissionsForDocument(@PathVariable UUID id) {
		return administrationService.getRightsForDocument(id);
	}
	
	@GetMapping("/folders/{id}")
	public List<GrantDTO> getPermissionsForFolder(@PathVariable UUID id) {
		return administrationService.getRightsForFolder(id);
	}

}
