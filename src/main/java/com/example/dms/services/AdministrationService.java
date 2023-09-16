package com.example.dms.services;

import com.example.dms.api.dtos.administration.GrantDTO;

import java.util.List;

public interface AdministrationService {

	List<GrantDTO> getRightsForDocument(Integer id);

	List<GrantDTO> getRightsForFolder(Integer id);

	List<GrantDTO> grantRightsForFolder(List<GrantDTO> dtos, Integer id);

	List<GrantDTO> grantRightsForDocument(List<GrantDTO> dtos, Integer id);

}
