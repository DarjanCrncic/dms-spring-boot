package com.example.dms.services;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import com.example.dms.api.dtos.group.DmsGroupDTO;
import com.example.dms.api.dtos.group.NewGroupDTO;
import com.example.dms.domain.DmsGroup;

public interface GroupService extends CrudService<DmsGroup, DmsGroupDTO, UUID>{

	DmsGroupDTO createNewGroup(@Valid NewGroupDTO groupDTO);

	DmsGroupDTO findGroupByGroupName(String groupName);

	DmsGroupDTO addUserToGroup(UUID groupId, UUID userId);

	DmsGroupDTO addUsersToGroup(UUID groupId, List<UUID> userList);

	DmsGroupDTO updateGroup(UUID id, NewGroupDTO groupDTO);

}
