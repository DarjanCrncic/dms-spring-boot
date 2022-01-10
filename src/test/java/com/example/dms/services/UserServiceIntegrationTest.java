package com.example.dms.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.api.dtos.user.UpdateUserDTO;
import com.example.dms.api.mappers.UserMapper;
import com.example.dms.domain.User;

@SpringBootTest
class UserServiceIntegrationTest {

	@Autowired
	UserMapper userMapper;
	
	@Autowired
	UserService userService;
	
	@Test
	@Transactional
	void userUpdateTest() {
		User user = userService.save(new User("testuser", "12345", "Darjan", "Crnčić", "test.test@gmail.com"));
		
		User updatedUser = userService.updateUser(new UpdateUserDTO("testuser2", "Darjana", "Crnčića", "test.user@gmaila.com"), user.getId());

		assertEquals(user.getId(), updatedUser.getId());
		assertEquals(user.getPassword(), updatedUser.getPassword());
		assertEquals("test.user@gmaila.com", updatedUser.getEmail());
		assertEquals("Darjana", updatedUser.getFirstName());
		assertEquals("Crnčića", updatedUser.getLastName());
		assertEquals("testuser2", updatedUser.getUsername());
	}
}
