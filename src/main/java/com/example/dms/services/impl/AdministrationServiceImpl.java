package com.example.dms.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.api.dtos.administration.GrantDTO;
import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsFolder;
import com.example.dms.domain.security.AclAllowedClass;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.services.AdministrationService;
import com.example.dms.services.DmsAclService;
import com.example.dms.utils.Permissions;
import com.example.dms.utils.exceptions.DmsNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdministrationServiceImpl implements AdministrationService {

	private final DmsAclService aclService;
	private final DocumentRepository documentRepository;
	private final FolderRepository folderRepository;

	private <T extends AclAllowedClass> List<GrantDTO> grantRightsToUsers(List<GrantDTO> dtos, T object) {
		Map<String, Set<String>> existingRights = grantDTOToMap(aclService.getRights(object));
		Map<String, Set<String>> newRights = grantDTOToMap(dtos);

		Set<String> usersToRemove = new HashSet<>(existingRights.keySet());
		usersToRemove.removeAll(newRights.keySet());
		for (String username : usersToRemove) {
			aclService.revokeRightsOnObject(object, username, null);
		}

		for (Entry<String, Set<String>> userEntry : newRights.entrySet()) {
			String username = userEntry.getKey();
			Set<String> existingRightsSet = new HashSet<>();
			if (existingRights.containsKey(username)) {
				existingRightsSet = existingRights.get(username);
			}

			Set<String> rightsToRemove = new HashSet<>(existingRightsSet);
			rightsToRemove.removeAll(newRights.get(username));
			if (!rightsToRemove.isEmpty()) {
				aclService.revokeRightsOnObject(object, username, Permissions.getByStrings(rightsToRemove));
			}

			Set<String> rightsToAdd = new HashSet<>(newRights.get(username));
			rightsToAdd.removeAll(existingRightsSet);
			if (!rightsToAdd.isEmpty()) {
				aclService.grantRightsOnObject(object, username, Permissions.getByStrings(rightsToAdd));
			}
		}

		return getRightsForObject(object);
	}

	@Override
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','ADMINISTRATION') || hasAuthority('ADMINISTRATION_PRIVILEGE')")
	public List<GrantDTO> grantRightsForDocument(List<GrantDTO> dtos, UUID id) {
		DmsDocument document = documentRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		return this.grantRightsToUsers(dtos, document);
	}

	@Override
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsFolder','ADMINISTRATION') || hasAuthority('ADMINISTRATION_PRIVILEGE')")
	public List<GrantDTO> grantRightsForFolder(List<GrantDTO> dtos, UUID id) {
		DmsFolder folder = folderRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		return this.grantRightsToUsers(dtos, folder);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public List<GrantDTO> getRightsForDocument(UUID id) {
		DmsDocument document = documentRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		return this.getRightsForObject(document);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public List<GrantDTO> getRightsForFolder(UUID id) {
		DmsFolder folder = folderRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		return this.getRightsForObject(folder);
	}

	private <T extends AclAllowedClass> List<GrantDTO> getRightsForObject(T entity) {
		return aclService.getRights(entity);
	}

	private Map<String, Set<String>> grantDTOToMap(List<GrantDTO> dtos) {
		return dtos.stream().collect(Collectors.toMap(GrantDTO::getUsername, GrantDTO::getPermissions));
	}
}
