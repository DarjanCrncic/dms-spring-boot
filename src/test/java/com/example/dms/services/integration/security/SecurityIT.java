package com.example.dms.services.integration.security;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
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
	
	@AfterEach
	void cleanUp() {
		if (user != null && userRepository.existsById(user.getId())) {
			userRepository.deleteById(user.getId());
		}
	}
	
	@Test
	@WithUserDetails("admin")
	void testSecurityWithUserDetails() {
		user = userService.saveNewUser(new NewUserDTO("testuser", "12345", "test", "test","test.test@gmail.com"));
		userService.deleteById(user.getId());
		
		assertTrue(userRepository.findByUsername("test").isEmpty());
	}
	
	@Test
	@WithMockUser(username = "testuser", authorities = { "ROLE_ADMIN" })
	void testSecurityWithMockUser() {
		user = userService.saveNewUser(new NewUserDTO("testuser", "12345", "test", "test","test.test@gmail.com"));
		userService.deleteById(user.getId());
		
		assertTrue(userRepository.findByUsername("test").isEmpty());
	}
	
	@Test
	@WithMockUser(username = "testuser", roles = { "ADMIN" })
	void testSecurityWithMockUserRple() {
		user = userService.saveNewUser(new NewUserDTO("testuser", "12345", "test", "test","test.test@gmail.com"));
		userService.deleteById(user.getId());
		
		assertTrue(userRepository.findByUsername("test").isEmpty());
	}
	
	@Test
	@WithMockUser(username = "testuser", authorities = { "ROLE_USER" })
	void testSecurityException() {
		NewUserDTO dto = new NewUserDTO("testuser", "12345", "test", "test","test.test@gmail.com");
		assertThrows(AccessDeniedException.class, () -> userService.saveNewUser(dto));
	}
	
}
