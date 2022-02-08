package com.example.dms.services.integration.security;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;

import com.example.dms.api.dtos.user.DmsUserDTO;
import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.UserService;

@SpringBootTest
@ContextConfiguration
class SecurityIT {

	
	@Autowired
	UserService userService;
	
	@Autowired
	UserRepository userRepository;
	
	DmsUserDTO user;
	
	@BeforeEach
	void setUp() {
		user = userService.saveNewUser(new NewUserDTO("testuser", "12345", "test", "test","test.test@gmail.com"));
	}
	
	@AfterEach
	void cleanUp() {
		if (userRepository.existsById(user.getId())) {
			userRepository.deleteById(user.getId());
		}
	}
	
	@Test
	@WithUserDetails("dmsadmin")
	void testSecurityWithUserDetails() {
		userService.deleteById(user.getId());
		
		assertTrue(userRepository.findByUsername("test").isEmpty());
	}
	
	@Test
	@WithMockUser(username = "tralal", authorities = { "ROLE_ADMIN" })
	void testSecurityWithMockUser() {
		userService.deleteById(user.getId());
		
		assertTrue(userRepository.findByUsername("test").isEmpty());
	}
	
	@Test
	@WithMockUser(username = "tralal", authorities = { "ROLE_USER" })
	void testSecurityException() {
		assertThrows(AccessDeniedException.class, () -> userService.deleteById(user.getId()));
	}

	
}
