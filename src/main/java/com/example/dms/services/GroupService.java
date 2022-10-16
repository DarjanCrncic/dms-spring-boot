package com.example.dms.services;

import com.example.dms.api.dtos.SortDTO;
import com.example.dms.api.dtos.group.DmsGroupDTO;
import com.example.dms.api.dtos.group.NewGroupDTO;
import com.example.dms.domain.DmsGroup;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface GroupService extends CrudService<DmsGroup, DmsGroupDTO, UUID>{

	DmsGroupDTO createGroup(@Valid NewGroupDTO groupDTO);

	DmsGroupDTO findGroupByGroupName(String groupName);

	DmsGroupDTO addUserToGroup(UUID groupId, UUID userId);

	DmsGroupDTO updateGroupMembers(UUID groupId, List<UUID> userList);

	DmsGroupDTO updateGroup(UUID id, NewGroupDTO groupDTO);

	List<DmsGroupDTO> searchAll(String search, SortDTO sort);
}
