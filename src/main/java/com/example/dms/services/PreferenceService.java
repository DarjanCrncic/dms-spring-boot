package com.example.dms.services;

import com.example.dms.domain.DmsDocumentColumnPreference;

import java.util.List;

public interface PreferenceService {

	List<DmsDocumentColumnPreference> getAllDocColPref(String username);

	List<DmsDocumentColumnPreference> saveDocColPref(String username, List<DmsDocumentColumnPreference> preferences);

}
