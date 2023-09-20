package com.example.dms.api.controllers;

import com.example.dms.domain.DmsDocumentColumnPreference;
import com.example.dms.security.DmsUserDetails;
import com.example.dms.services.PreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/preferences")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class PreferenceController {

	private final PreferenceService preferenceService;

	@GetMapping("/document-columns")
	public List<DmsDocumentColumnPreference> getDocumentColumnPreferences(
			@AuthenticationPrincipal DmsUserDetails userDetails) {
		return preferenceService.getAllDocColPref(userDetails.getUsername());
	}

	@PostMapping("/document-columns")
	public List<DmsDocumentColumnPreference> saveDocumentColumnPreferences(
			@AuthenticationPrincipal DmsUserDetails userDetails,
			@RequestBody List<DmsDocumentColumnPreference> preferences) {
		return preferenceService.saveDocColPref(userDetails.getUsername(), preferences);
	}
}
