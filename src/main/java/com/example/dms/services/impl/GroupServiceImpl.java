package com.example.dms.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.api.dtos.group.DmsGroupDTO;
import com.example.dms.api.dtos.group.NewGroupDTO;
import com.example.dms.api.mappers.GroupMapper;
import com.example.dms.domain.DmsGroup;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.GroupRepository;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.GroupService;
import com.example.dms.utils.exceptions.NotFoundException;
import com.example.dms.utils.exceptions.UniqueConstraintViolatedException;

@Service
@Transactional
public class GroupServiceImpl extends EntityCrudServiceImpl<DmsGroup, DmsGroupDTO> implements GroupService{

	GroupRepository groupRepository;
	UserRepository userRepository;
	GroupMapper groupMapper;
	
	public GroupServiceImpl(GroupRepository groupRepository, UserRepository userRepository, GroupMapper groupMapper) {
		super(groupRepository, groupMapper);
		this.groupMapper = groupMapper;
		this.groupRepository = groupRepository;
		this.userRepository = userRepository;
	}

	@Override
	public DmsGroupDTO addUserToGroup(UUID groupId, UUID userId) {
		DmsGroup group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group with specified id was not found."));
		DmsUser user = null;
		user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with specified id was not found."));
		group.getMembers().add(user);
		return save(group);
	}
	
	@Override
	public DmsGroupDTO addUsersToGroup(UUID groupId, List<UUID> userIdList) {
		DmsGroup group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group with specified id was not found."));
		DmsUser user = null;
		for (UUID id : userIdList) {
			user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with specified id was not found."));
			group.getMembers().add(user);
		}
		return save(group);
	}
	
	@Override
	public DmsGroupDTO findGroupByGroupName(String groupName) {
		Optional<DmsGroup> existingGroup = groupRepository.findByGroupName(groupName);
		if (existingGroup.isEmpty()) {
			throw new UniqueConstraintViolatedException("Group name: '" + groupName + "' is not found.");
		}
		return groupMapper.entityToDto(existingGroup.get());
	}
	
	@Override
	public DmsGroupDTO createNewGroup(NewGroupDTO dmsGroup) {
		Optional<DmsGroup> existingGroup = groupRepository.findByGroupName(dmsGroup.getGroupName());
		if (existingGroup.isPresent()) {
			throw new UniqueConstraintViolatedException("Following field is not unique: groupName, value: '" + dmsGroup.getGroupName() + "'");
		}
		return save(groupMapper.newGroupDtoToGroup(dmsGroup));
	}

	@Override
	public DmsGroupDTO updateGroup(UUID id, NewGroupDTO groupDTO) {
		DmsGroup existingGroup = groupRepository.findById(id).orElseThrow(NotFoundException::new);
		existingGroup.setDescription(groupDTO.getDescription());
		existingGroup.setGroupName(groupDTO.getGroupName());
		return save(existingGroup);
	}
}
