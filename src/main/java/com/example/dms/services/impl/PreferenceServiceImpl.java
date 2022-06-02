package com.example.dms.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.dms.domain.DmsDocumentColumnPreference;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.DocumentColumnPreferencesRepository;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.PreferenceService;
import com.example.dms.utils.exceptions.BadRequestException;
import com.example.dms.utils.exceptions.DmsNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PreferenceServiceImpl implements PreferenceService {

	private final DocumentColumnPreferencesRepository documentColumnPreferencesRepository;
	private final UserRepository userRepository;

	@Value("${document.column.preferences.identifiers}")
	private List<String> docColIdentifiers;

	@Value("${document.column.preferences.titles}")
	private List<String> docColTitles;

	@Override
	public List<DmsDocumentColumnPreference> getAllDocColPref(String username) {
		DmsUser user = userRepository.findByUsername(username)
				.orElseThrow(() -> new DmsNotFoundException("User with username: " + username + " not found."));

		List<DmsDocumentColumnPreference> preferences = new ArrayList<>();
		for (int i = 0; i < docColIdentifiers.size(); i++) {
			DmsDocumentColumnPreference pref = documentColumnPreferencesRepository
					.findByIdentifierAndUserUsername(docColIdentifiers.get(i), username).orElse(null);
			if (pref == null) {
				pref = generateDefaultDocumentColumnPreferences(user, docColIdentifiers.get(i), docColTitles.get(i));
			}
			preferences.add(pref);
		}
		return preferences;
	}

	private DmsDocumentColumnPreference generateDefaultDocumentColumnPreferences(DmsUser user, String identifier,
			String title) {

		DmsDocumentColumnPreference pref = DmsDocumentColumnPreference.builder().identifier(identifier).title(title)
				.displayed(true).user(user).build();
		return documentColumnPreferencesRepository.save(pref);
	}

	@Override
	public List<DmsDocumentColumnPreference> saveDocColPref(String username,
			List<DmsDocumentColumnPreference> preferences) {
		return preferences.stream().map(pref -> {
			DmsDocumentColumnPreference savedPref = documentColumnPreferencesRepository
					.findByIdentifierAndUserUsername(pref.getIdentifier(), username).orElseThrow(
							() -> new BadRequestException("invalid preference identifier " + pref.getIdentifier()));
			savedPref.setDisplayed(pref.isDisplayed());
			return documentColumnPreferencesRepository.save(savedPref);
		}).collect(Collectors.toList());
	}
}
