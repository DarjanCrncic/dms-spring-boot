package com.example.dms.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.dms.api.dtos.group.NewGroupDTO;
import com.example.dms.domain.DmsGroup;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.GroupRepository;
import com.example.dms.services.GroupService;
import com.example.dms.services.UserService;
import com.example.dms.utils.exceptions.UniqueConstraintViolatedException;

@Service
public class GroupServiceImpl extends EntityCrudServiceImpl<DmsGroup> implements GroupService{

	GroupRepository groupRepository;
	UserService userService;
	
	public GroupServiceImpl(GroupRepository groupRepository, UserService userService) {
		super();
		this.groupRepository = groupRepository;
		this.userService = userService;
	}

	@Override
	public DmsGroup addUserToGroup(UUID groupId, UUID userId) {
		DmsGroup group = findById(groupId);
		DmsUser user = null;
		user = userService.findById(userId);
		group.getMembers().add(user);
		return groupRepository.save(group);
	}
	
	@Override
	public DmsGroup addUsersToGroup(UUID groupId, List<UUID> userIdList) {
		DmsGroup group = findById(groupId);
		DmsUser user = null;
		for (UUID id : userIdList) {
			user = userService.findById(id);
			group.getMembers().add(user);
		}
		return groupRepository.save(group);
	}
	
	@Override
	public DmsGroup findGroupByGroupName(String groupName) {
		Optional<DmsGroup> existingGroup = groupRepository.findByGroupName(groupName);
		if (existingGroup.isEmpty()) {
			throw new UniqueConstraintViolatedException("Group name: '" + groupName + "' is not found.");
		}
		return existingGroup.get();
	}
	
	@Override
	public DmsGroup createNewGroup(DmsGroup dmsGroup) {
		Optional<DmsGroup> existingGroup = groupRepository.findByGroupName(dmsGroup.getGroupName());
		if (existingGroup.isPresent()) {
			throw new UniqueConstraintViolatedException("Following field is not unique: groupName, value: '" + dmsGroup.getGroupName() + "'");
		}
		return groupRepository.save(dmsGroup);
	}

	@Override
	public DmsGroup updateGroup(UUID id, NewGroupDTO groupDTO) {
		DmsGroup existingGroup = findById(id);
		existingGroup.setDescription(groupDTO.getDescription());
		existingGroup.setGroupName(groupDTO.getGroupName());
		return groupRepository.save(existingGroup);
	}
}
