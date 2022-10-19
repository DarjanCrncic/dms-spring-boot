package com.example.dms.services.impl;

import com.example.dms.api.dtos.SortDTO;
import com.example.dms.api.dtos.group.DmsGroupDTO;
import com.example.dms.api.dtos.group.NewGroupDTO;
import com.example.dms.api.mappers.GroupMapper;
import com.example.dms.domain.DmsGroup;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.GroupRepository;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.DmsAclService;
import com.example.dms.services.GroupService;
import com.example.dms.services.search.SpecificationBuilder;
import com.example.dms.services.search.group.GroupSpecProvider;
import com.example.dms.utils.Utils;
import com.example.dms.utils.exceptions.DmsNotFoundException;
import com.example.dms.utils.exceptions.UniqueConstraintViolatedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@PreAuthorize("hasRole('ADMIN')")
public class GroupServiceImpl extends EntityCrudServiceImpl<DmsGroup, DmsGroupDTO> implements GroupService{

	private final GroupRepository groupRepository;
	private final UserRepository userRepository;
	private final GroupMapper groupMapper;

	public GroupServiceImpl(GroupRepository groupRepository, UserRepository userRepository, GroupMapper groupMapper, DmsAclService aclService) {
		super(groupRepository, groupMapper, aclService);
		this.groupMapper = groupMapper;
		this.groupRepository = groupRepository;
		this.userRepository = userRepository;
	}

	@Override
	public List<DmsGroupDTO> findAll() {
		return groupMapper.entityListToDtoList(groupRepository.findAll());
	}

	@Override
	public DmsGroupDTO addUserToGroup(UUID groupId, UUID userId) {
		DmsGroup group = groupRepository.findById(groupId).orElseThrow(() -> new DmsNotFoundException("Group with specified id was not found."));
		DmsUser user = userRepository.findById(userId).orElseThrow(() -> new DmsNotFoundException("User with specified id was not found."));
		group.getMembers().add(user);
		return save(group);
	}

	@Override
	public DmsGroupDTO updateGroupMembers(UUID groupId, List<UUID> userIdList) {
		DmsGroup group = groupRepository.findById(groupId).orElseThrow(() -> new DmsNotFoundException("Group with specified id was not found."));
		List<DmsUser> users = userRepository.findAllByIdIn(userIdList);
		if (users.size() != userIdList.size()) {
			throw new DmsNotFoundException("Invalid user id list provided.");
		}
		group.setMembers(new HashSet<>(users));
		return save(group);
	}

	@Override
	public DmsGroupDTO createGroup(NewGroupDTO groupDTO) {
		checkGroup(groupDTO.getGroupName(), groupDTO.getIdentifier(), null);
		return save(groupMapper.newGroupDtoToGroup(groupDTO));
	}

	@Override
	public DmsGroupDTO updateGroup(UUID id, NewGroupDTO groupDTO) {
		DmsGroup existingGroup = groupRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		checkGroup(groupDTO.getGroupName(), groupDTO.getIdentifier(), existingGroup.getId());
		groupMapper.updateGroupPut(groupDTO, existingGroup);
		return save(existingGroup);
	}

	private void checkGroup(String groupName, String identifier, UUID groupId) {
		Optional<DmsGroup> existingGroupName = groupRepository.findByGroupName(groupName);
		Optional<DmsGroup> existingGroupIdentifier = groupRepository.findByGroupName(identifier);

		if (existingGroupName.isPresent() && (groupId == null || !existingGroupName.get().getId().equals(groupId))) {
			throw new UniqueConstraintViolatedException("Group name must be unique.");
		}
		if (existingGroupIdentifier.isPresent() && (groupId == null || !existingGroupIdentifier.get().getId().equals(groupId))) {
			throw new UniqueConstraintViolatedException("Group identifier must be unique.");
		}
	}

	@Override
	public List<DmsGroupDTO> searchAll(String search, SortDTO sort) {
		if (search != null) {
			SpecificationBuilder<DmsGroup> builder = new SpecificationBuilder<>(new GroupSpecProvider());
			return groupMapper
					.entityListToDtoList(groupRepository.findAll(builder.parse(search), Utils.toSort(sort)));
		}
		return groupMapper.entityListToDtoList(groupRepository.findAll(Utils.toSort(sort)));
	}
}
