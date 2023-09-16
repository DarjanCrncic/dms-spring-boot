package com.example.dms.services.integration;

import com.example.dms.api.dtos.group.DmsGroupDTO;
import com.example.dms.api.dtos.group.NewGroupDTO;
import com.example.dms.api.dtos.user.DmsUserDTO;
import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.repositories.GroupRepository;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.GroupService;
import com.example.dms.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration
@WithMockUser(roles = "ADMIN")
class GroupServiceIT {

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;

	@Autowired
	GroupService groupService;

	DmsUserDTO user1, user2;
	DmsGroupDTO savedGroup;
	NewGroupDTO newGroupDTO;

	@BeforeEach
	void setUp() {
		user1 = userService.createUser(new NewUserDTO("testuser", "12345", "Darjan", "Crnčić", "test.user@gmail.com",
				"ROLE_USER",
				new ArrayList<>()));
		user2 = userService.createUser(new NewUserDTO("testuser2", "12345", "Darjan", "Crnčić", "test2.user@gmail.com",
				"ROLE_USER", new ArrayList<>()));
		newGroupDTO = NewGroupDTO.builder().groupName("GROUP").identifier("GROUP").description("test group").build();
		savedGroup = groupService.createGroup(newGroupDTO);
	}

	@AfterEach
	void cleanUp() {
		if (savedGroup != null && groupRepository.existsById(savedGroup.getId()))
			groupRepository.deleteById(savedGroup.getId());
		if (user1 != null && userRepository.existsById(user1.getId()))
			userRepository.deleteById(user1.getId());
		if (user2 != null && userRepository.existsById(user2.getId()))
			userRepository.deleteById(user2.getId());
	}

	@Test
	@DisplayName("Test creation of a new group.")
	void createNewGroupTest() {
		assertEquals(newGroupDTO.getGroupName(), savedGroup.getGroupName());
		assertEquals(newGroupDTO.getDescription(), savedGroup.getDescription());
		assertEquals(0, savedGroup.getMembers().size());
	}

	@Test
	@DisplayName("Test adding users to group.")
	@Transactional
	void addUsersToGroup() {
		savedGroup = groupService.addUserToGroup(savedGroup.getId(), user1.getId());
		savedGroup = groupService.addUserToGroup(savedGroup.getId(), user2.getId());
		savedGroup = groupService.addUserToGroup(savedGroup.getId(), user2.getId());

		assertEquals(2, savedGroup.getMembers().size());
	}

	@Test
	@DisplayName("Test adding multiple users to group.")
	@Transactional
	void addMultipleUsersToGroup() {
		List<Integer> userList = Arrays.asList(user1.getId(), user2.getId());
		savedGroup = groupService.updateGroupMembers(savedGroup.getId(), userList);

		assertEquals(2, savedGroup.getMembers().size());
	}


}
