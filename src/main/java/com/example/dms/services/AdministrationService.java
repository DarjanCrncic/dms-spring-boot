package com.example.dms.services;

import com.example.dms.api.dtos.administration.GrantDTO;
import com.example.dms.domain.security.AclAllowedClass;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AdministrationService {

	List<GrantDTO> getRightsForDocument(UUID id);

	List<GrantDTO> getRightsForFolder(UUID id);

	List<GrantDTO> grantRightsForFolder(List<GrantDTO> dtos, UUID id);

	List<GrantDTO> grantRightsForDocument(List<GrantDTO> dtos, UUID id);

	<T extends AclAllowedClass> Set<String> getRecipients(T object);

	<T extends AclAllowedClass> Set<String> getRecipients(T object, String filterPermission);
}
