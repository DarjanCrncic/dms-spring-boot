package com.example.dms.services.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.api.dtos.group.DmsGroupDTO;
import com.example.dms.api.dtos.group.NewGroupDTO;
import com.example.dms.api.dtos.user.DmsUserDTO;
import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.repositories.GroupRepository;
import com.example.dms.services.GroupService;
import com.example.dms.services.UserService;

@SpringBootTest
@ContextConfiguration
@WithMockUser(roles = "ADMIN")
class GroupServiceIT {

	@Autowired
	GroupRepository groupRepository;
	
	@Autowired
	UserService userService;
	
	@Autowired
	GroupService groupService;
	
	DmsUserDTO user1, user2;
	DmsGroupDTO savedGroup;
	NewGroupDTO newGroupDTO;
	
	@BeforeEach
	void setUp() {
		user1 = userService.saveNewUser(new NewUserDTO("testuser", "12345", "Darjan", "Crnčić", "test.user@gmail.com"));
		user2 = userService.saveNewUser(new NewUserDTO("testuser2", "12345", "Darjan", "Crnčić", "test2.user@gmail.com"));
		newGroupDTO = NewGroupDTO.builder().groupName("grupa").description("testna grupa").build();
		savedGroup = groupService.createNewGroup(newGroupDTO);
	}
	
	@AfterEach
	void cleanUp() {
		userService.deleteById(user1.getId());
		userService.deleteById(user2.getId());
		groupService.deleteById(savedGroup.getId());
	}
	
	@Test
	void createNewGroupTest() {
		assertEquals(newGroupDTO.getGroupName(), savedGroup.getGroupName());
		assertEquals(newGroupDTO.getDescription(), savedGroup.getDescription());
		assertEquals(0, savedGroup.getMembers().size());
	}
	@Test
	@Transactional
	void addUsersToGroup() {
		savedGroup = groupService.addUserToGroup(savedGroup.getId(), user1.getId());
		savedGroup = groupService.addUserToGroup(savedGroup.getId(), user2.getId());
		savedGroup = groupService.addUserToGroup(savedGroup.getId(), user2.getId());
		
		assertEquals(2, savedGroup.getMembers().size());
	}
	@Test
	@Transactional
	void addMultipleUsersToGroup() {
		List<UUID> userList = Arrays.asList(new UUID[]{user1.getId(), user2.getId()});
		savedGroup = groupService.addUsersToGroup(savedGroup.getId(), userList);
		
		assertEquals(2, savedGroup.getMembers().size());
	}
	
	
}
