package com.example.dms.services.impl;

import com.example.dms.api.dtos.administration.GrantDTO;
import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsFolder;
import com.example.dms.domain.security.AclAllowedClass;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.services.AdministrationService;
import com.example.dms.services.DmsAclService;
import com.example.dms.services.NotificationService;
import com.example.dms.utils.ActionEnum;
import com.example.dms.utils.Permissions;
import com.example.dms.utils.Utils;
import com.example.dms.utils.exceptions.DmsNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.dms.utils.Permissions.READ;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class AdministrationServiceImpl implements AdministrationService {

	private final DmsAclService aclService;
	private final DocumentRepository documentRepository;
	private final FolderRepository folderRepository;
	private final NotificationService notificationService;

	private <T extends AclAllowedClass> List<GrantDTO> grantRightsToSid(List<GrantDTO> dtos, T object) {
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

		return aclService.getRights(object);
	}

	@Override
	@PreAuthorize("hasAuthority('ADMINISTRATION_PRIVILEGE') || hasPermission(#id,'com.example.dms.domain.DmsDocument','ADMINISTRATION')")
	public List<GrantDTO> grantRightsForDocument(List<GrantDTO> dtos, Integer id) {
		DmsDocument document = documentRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		List<GrantDTO> granted = this.grantRightsToSid(dtos, document);

		List<GrantDTO> readDtos = dtos.stream()
				.map(dto -> new GrantDTO(dto.getUsername(), Utils.toSet(READ.name())))
				.collect(Collectors.toList());
		this.grantReadRightsToAllParentFolders(readDtos, document.getParentFolder());

		notificationService.createAclNotification(document, ActionEnum.ADMINISTRATE);
		return granted;
	}

	@Override
	@PreAuthorize("hasAuthority('ADMINISTRATION_PRIVILEGE') || hasPermission(#id,'com.example.dms.domain.DmsFolder','ADMINISTRATION')")
	public List<GrantDTO> grantRightsForFolder(List<GrantDTO> dtos, Integer id) {
		DmsFolder folder = folderRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		List<GrantDTO> granted = this.grantRightsToSid(dtos, folder);

		List<GrantDTO> readDtos = dtos.stream()
				.map(dto -> new GrantDTO(dto.getUsername(), Utils.toSet(READ.name())))
				.collect(Collectors.toList());
		this.grantReadRightsToAllParentFolders(readDtos, folder.getParentFolder());

		notificationService.createAclNotification(folder, ActionEnum.ADMINISTRATE);
		return granted;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public List<GrantDTO> getRightsForDocument(Integer id) {
		DmsDocument document = documentRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		return aclService.getRights(document);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public List<GrantDTO> getRightsForFolder(Integer id) {
		DmsFolder folder = folderRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		return aclService.getRights(folder);
	}

	private Map<String, Set<String>> grantDTOToMap(List<GrantDTO> dtos) {
		return dtos.stream().collect(Collectors.toMap(GrantDTO::getUsername, GrantDTO::getPermissions));
	}

	private void grantReadRightsToAllParentFolders(List<GrantDTO> dtos, DmsFolder parentFolder) {
		if (!parentFolder.isRoot()) {
			log.info("Granting READ to parent folder: {}", parentFolder.getName());
			this.grantRightsToSid(dtos, parentFolder);
			grantReadRightsToAllParentFolders(dtos, parentFolder.getParentFolder());
		}
	}
}
