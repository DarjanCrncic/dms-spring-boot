package com.example.dms.services;

import java.util.List;
import java.util.UUID;

import com.example.dms.api.dtos.administration.GrantDTO;

public interface AdministrationService {

	List<GrantDTO> getRightsForDocument(UUID id);

	List<GrantDTO> getRightsForFolder(UUID id);

	List<GrantDTO> grantRightsForFolder(List<GrantDTO> dtos, UUID id);

	List<GrantDTO> grantRightsForDocument(List<GrantDTO> dtos, UUID id);

}
