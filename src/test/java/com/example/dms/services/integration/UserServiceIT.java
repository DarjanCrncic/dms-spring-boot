package com.example.dms.services.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import com.example.dms.api.dtos.user.DmsUserDTO;
import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UpdateUserDTO;
import com.example.dms.api.mappers.UserMapper;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.UserService;
import com.example.dms.utils.exceptions.DmsNotFoundException;
import com.example.dms.utils.exceptions.UniqueConstraintViolatedException;

@SpringBootTest
@ContextConfiguration
@WithMockUser(roles = "ADMIN")
class UserServiceIT {

	@Autowired
	UserMapper userMapper;
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserRepository userRepository;
	
	DmsUserDTO user;
	
	@BeforeEach
	void setUp() {
		user = userService.createUser(new NewUserDTO("testuser", "12345", "test", "test","test.test@gmail.com"));
	}
	
	@AfterEach
	void cleanUp() {
		if (user != null && userRepository.existsById(user.getId())) {
			userRepository.deleteById(user.getId());
		}
	}
	
	@Test
	@DisplayName("Test creating users.")
	void userCreateTest() {
		NewUserDTO userDTO = new NewUserDTO("testuser", "12345", "test", "test","test1.test@gmail.com");
		assertThrows(UniqueConstraintViolatedException.class, () -> userService.createUser(userDTO));
		
		NewUserDTO secondUserDTO = new NewUserDTO("testuser1", "12345", "test", "test","test.test@gmail.com");
		assertThrows(UniqueConstraintViolatedException.class, () -> userService.createUser(secondUserDTO));
	}
	
	@Test
	@DisplayName("Test updating users with HTTP PUT request.")
	void userUpdateTestPut() {
		DmsUserDTO updatedUser = userService.updateUser(new UpdateUserDTO("testuser2", "Darjana", "Crnčića", "test.user@gmaila.com"), user.getId(), false);

		assertEquals(user.getId(), updatedUser.getId());
		assertEquals("test.user@gmaila.com", updatedUser.getEmail());
		assertEquals("Darjana", updatedUser.getFirstName());
		assertEquals("Crnčića", updatedUser.getLastName());
		assertEquals("testuser2", updatedUser.getUsername());
	}
	
	@Test
	@DisplayName("Test updating users with HTTP PATCH request.")
	void userUpdateTestPatch() {
		DmsUserDTO updatedUser = userService.updateUser(new UpdateUserDTO("testuser2", null, null, "test.user@gmaila.com"), user.getId(), true);
		
		assertEquals(user.getId(), updatedUser.getId());
		assertEquals("test.user@gmaila.com", updatedUser.getEmail());
		assertEquals(user.getFirstName(), updatedUser.getFirstName());
		assertEquals(user.getLastName(), updatedUser.getLastName());
		assertEquals("testuser2", updatedUser.getUsername());
	}
	
	@Test
	@DisplayName("Test finding user by username.")
	void findUserByUsername() {
		assertEquals(user.getId(), userService.findByUsername(user.getUsername()).getId());
		assertThrows(DmsNotFoundException.class, () -> userService.findByUsername("test"));
	}

	@Test
	@DisplayName("Test finding user by email.")
	void findUserByEmail() {
		assertEquals(user.getId(), userService.findByEmail(user.getEmail()).getId());
		assertThrows(DmsNotFoundException.class, () -> userService.findByEmail("test"));
	}
	
}
