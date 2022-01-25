package com.example.dms.api.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UserDTO;
import com.example.dms.api.mappers.UserMapper;
import com.example.dms.domain.DmsUser;
import com.example.dms.services.UserService;
import com.example.dms.utils.exceptions.NotFoundException;
import com.example.dms.utils.exceptions.UniqueConstraintViolatedException;

@WebMvcTest(UserController.class)
class UserControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	UserService userService;

	@MockBean
	UserMapper userMapper;

	DmsUser validUser;
	UserDTO validUserDTO;
	String validUserJSON;

	@BeforeEach
	void setUp() {
		validUser = DmsUser.builder().username("dcrncic").password("12345").firstName("Darjan").lastName("Crnčić")
				.email("darjan.crncic@gmail.com").build();
		validUser.setId(UUID.randomUUID());

		validUserDTO = UserDTO.builder().id(validUser.getId()).username("dcrncic").firstName("Darjan")
				.lastName("Crnčić").email("darjan.crncic@gmail.com").creationDate(LocalDateTime.now())
				.modifyDate(LocalDateTime.now()).build();
		validUserJSON = "{\n    \"password\": \"12345\",\n    \"username\": \"dcrncic\",\n    \"first_name\": \"Darjan\",\n    \"last_name\": \"Crn\u010di\u0107\",\n    \"email\": \"darjan.crncic@gmail.com\"\n}";
	}

	@Test
	void testGetUserById() throws Exception {

		BDDMockito.given(userService.findById(Mockito.any())).willReturn(validUserDTO);

		mockMvc.perform(get("/api/v1/users/{id}", validUser.getId())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(validUser.getId().toString())))
				.andExpect(jsonPath("$.username", is(validUser.getUsername())))
				.andExpect(jsonPath("$.email", is(validUser.getEmail()))).andReturn();
	}

	@Test
	void testGetUserByIdNotFound() throws Exception {
		BDDMockito.given(userService.findById(Mockito.any())).willThrow(NotFoundException.class);

		mockMvc.perform(get("/api/v1/users/{id}", UUID.randomUUID())).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	void testGetUserByUsername() throws Exception {
		BDDMockito.given(userService.findByUsername(Mockito.any(String.class))).willReturn(validUserDTO);

		mockMvc.perform(get("/api/v1/users").param("username", validUser.getUsername())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(validUser.getId().toString())))
				.andExpect(jsonPath("$.username", is(validUser.getUsername())))
				.andExpect(jsonPath("$.email", is(validUser.getEmail()))).andReturn();
	}

	@Test
	void testGetUserWithInvalidParam() throws Exception {
		mockMvc.perform(get("/api/v1/users").param("username2", validUser.getUsername()))
				.andExpect(status().isBadRequest()).andReturn();
	}

	@Test
	void testCreateNewUser() throws Exception {
		BDDMockito.given(userService.saveNewUser(Mockito.any(NewUserDTO.class))).willReturn(validUserDTO);

		mockMvc.perform(post("/api/v1/users").content(validUserJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();
	}

	@Test
	void testCreateNewUserUnique() throws Exception {
		BDDMockito.given(userService.saveNewUser(Mockito.any(NewUserDTO.class)))
				.willThrow(UniqueConstraintViolatedException.class);

		mockMvc.perform(post("/api/v1/users").content(validUserJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andReturn();
	}

}
