package com.example.dms.services;

import java.util.List;

import com.example.dms.domain.DmsDocumentColumnPreference;

public interface PreferenceService {

	List<DmsDocumentColumnPreference> getAllDocColPref(String username);

	List<DmsDocumentColumnPreference> saveDocColPref(String username, List<DmsDocumentColumnPreference> preferences);

}
