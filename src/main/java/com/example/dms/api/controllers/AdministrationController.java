package com.example.dms.api.controllers;

import com.example.dms.api.dtos.administration.GrantDTO;
import com.example.dms.api.dtos.administration.RolesPrivilegesDTO;
import com.example.dms.services.AdministrationService;
import com.example.dms.utils.Privileges;
import com.example.dms.utils.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/administration")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class AdministrationController {
	
	private final AdministrationService administrationService;

	@PostMapping("/documents/grant/{id}")
	public List<GrantDTO> grantPermissionsDocuments(@RequestBody List<GrantDTO> grantDTOs, @PathVariable Integer id) {
		return administrationService.grantRightsForDocument(grantDTOs, id);
	}
	
	@PostMapping("/folders/grant/{id}")
	public List<GrantDTO> grantPermissionsFolders(@RequestBody List<GrantDTO> grantDTOs, @PathVariable Integer id) {
		return administrationService.grantRightsForFolder(grantDTOs, id);
	}
	
	@GetMapping("/documents/{id}")
	public List<GrantDTO> getPermissionsForDocument(@PathVariable Integer id) {
		return administrationService.getRightsForDocument(id);
	}
	
	@GetMapping("/folders/{id}")
	public List<GrantDTO> getPermissionsForFolder(@PathVariable Integer id) {
		return administrationService.getRightsForFolder(id);
	}

	@GetMapping("/roles-privileges")
	public RolesPrivilegesDTO getRolesAndPrivileges() {
		return new RolesPrivilegesDTO(Roles.getAsString(), Privileges.getAsString());
	}

}
