package com.example.dms.services;

import java.util.List;
import java.util.UUID;

import com.example.dms.api.dtos.administration.GrantDTO;

public interface AdministrationService {

	List<GrantDTO> grantRightsToUsers(List<GrantDTO> dtos, UUID id);

	List<GrantDTO> getRightsForDocument(UUID id);

}
