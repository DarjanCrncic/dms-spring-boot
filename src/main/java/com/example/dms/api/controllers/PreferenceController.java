package com.example.dms.api.controllers;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dms.domain.DmsDocumentColumnPreference;
import com.example.dms.security.DmsUserDetails;
import com.example.dms.services.PreferenceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/preferences")
@RequiredArgsConstructor
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
