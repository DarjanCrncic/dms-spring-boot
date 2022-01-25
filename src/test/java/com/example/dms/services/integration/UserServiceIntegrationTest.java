package com.example.dms.services.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UpdateUserDTO;
import com.example.dms.api.dtos.user.UserDTO;
import com.example.dms.api.mappers.UserMapper;
import com.example.dms.services.UserService;
import com.example.dms.utils.exceptions.NotFoundException;
import com.example.dms.utils.exceptions.UniqueConstraintViolatedException;

@SpringBootTest
class UserServiceIntegrationTest {

	@Autowired
	UserMapper userMapper;
	
	@Autowired
	UserService userService;
	
	UserDTO user;
	
	@BeforeEach
	void setUp() {
		user = userService.saveNewUser(new NewUserDTO("testuser", "12345", "test", "test","test.test@gmail.com"));
	}
	
	@Test
	@Transactional
	void userCreateTest() {
		NewUserDTO userDTO = new NewUserDTO("testuser", "12345", "test", "test","test1.test@gmail.com");
		assertThrows(UniqueConstraintViolatedException.class, () -> userService.saveNewUser(userDTO));
		
		NewUserDTO secondUserDTO = new NewUserDTO("testuser1", "12345", "test", "test","test.test@gmail.com");
		assertThrows(UniqueConstraintViolatedException.class, () -> userService.saveNewUser(secondUserDTO));
	}
	
	@Test
	@Transactional
	void userUpdateTest() {
		
		UserDTO updatedUser = userService.updateUser(new UpdateUserDTO("testuser2", "Darjana", "Crnčića", "test.user@gmaila.com"), user.getId());

		assertEquals(user.getId(), updatedUser.getId());
		assertEquals("test.user@gmaila.com", updatedUser.getEmail());
		assertEquals("Darjana", updatedUser.getFirstName());
		assertEquals("Crnčića", updatedUser.getLastName());
		assertEquals("testuser2", updatedUser.getUsername());
	}
	
	@Test
	@Transactional
	void findUserByUsername() {
		
		assertEquals(user, userService.findByUsername(user.getUsername()));
		
		assertThrows(NotFoundException.class, () -> userService.findByUsername("test"));
	}

	@Test
	@Transactional
	void findUserByEmail() {
		
		assertEquals(user, userService.findByEmail(user.getEmail()));
		
		assertThrows(NotFoundException.class, () -> userService.findByEmail("test"));
	}
	
}
