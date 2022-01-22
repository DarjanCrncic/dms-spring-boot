package com.example.dms.services;

import java.util.List;
import java.util.UUID;

import com.example.dms.api.dtos.group.NewGroupDTO;
import com.example.dms.domain.DmsGroup;

public interface GroupService extends CrudService<DmsGroup, UUID>{

	DmsGroup createNewGroup(DmsGroup dmsGroup);

	DmsGroup findGroupByGroupName(String groupName);

	DmsGroup addUserToGroup(UUID groupId, UUID userId);

	DmsGroup addUsersToGroup(UUID groupId, List<UUID> userList);

	DmsGroup updateGroup(UUID id, NewGroupDTO groupDTO);

}
