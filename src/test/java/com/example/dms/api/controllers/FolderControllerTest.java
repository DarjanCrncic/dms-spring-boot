package com.example.dms.api.controllers;

import com.example.dms.api.dtos.folder.DmsFolderDTO;
import com.example.dms.api.dtos.folder.NewFolderDTO;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.FolderService;
import com.example.dms.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FolderController.class)
@ContextConfiguration
@WithMockUser(username = "admin", roles = { "ADMIN" })
class FolderControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	FolderService folderService;

	@MockBean
	UserDetailsService userDetailsService;

	@MockBean
	UserRepository userRepository;

	DmsUser validUser;
	DmsFolderDTO rootFolderDTO;
	DmsFolderDTO validFolderDTO;
	List<DmsFolderDTO> folderList;

	private static final String BASE_URL = "/api/v1/folders";

	@BeforeEach
	void setUp() {
		validUser = DmsUser.builder().username("dcrncic").password("12345").firstName("Darjan").lastName("Crnčić")
				.email("darjan.crncic@gmail.com").build();
		validUser.setId(UUID.randomUUID());

		rootFolderDTO = DmsFolderDTO.builder().name("/").parentFolderId(null).build();
		validFolderDTO = DmsFolderDTO.builder().name("test").build();

		folderList = new ArrayList<DmsFolderDTO>();
		folderList.add(rootFolderDTO);
		folderList.add(validFolderDTO);
	}

	@Test
	void testGetAllFolders() throws Exception {
		BDDMockito.given(folderService.findAll()).willReturn(folderList);

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
	}

	@Test
	void testFindById() throws Exception {
		BDDMockito.given(folderService.findById(Mockito.any(UUID.class))).willReturn(validFolderDTO);

		mockMvc.perform(get(BASE_URL + "/{id}", UUID.randomUUID())).andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is(validFolderDTO.getName())))
				.andExpect(jsonPath("$.subfolders").isArray()).andExpect(jsonPath("$.parent_folder").isString());

	}

	// TODO
//	@Test
//	void testSaveNewFolder() throws Exception {
//		BDDMockito.given(folderService.createFolder(Mockito.anyString(), Mockito.anyString())).willReturn(validFolderDTO);
//		mockMvc.perform(post(BASE_URL).content(Utils.stringify(new NewFolderDTO("/test"))).contentType(MediaType.APPLICATION_JSON))
//				.andExpect(status().isCreated())
//				.andExpect(jsonPath("$.path", is(validFolderDTO.getPath())))
//				.andExpect(jsonPath("$.subfolders").isArray())
//				.andExpect(jsonPath("$.parent_folder").isString());
//	}

	@Test
	void testUpdateFolder() throws Exception {
		BDDMockito.given(folderService.updateFolder(Mockito.any(UUID.class), Mockito.anyString()))
				.willReturn(validFolderDTO);

		mockMvc.perform(put(BASE_URL + "/{id}", UUID.randomUUID())
				.content(Utils.stringify(NewFolderDTO.builder().name("test").build()))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is(validFolderDTO.getName())))
				.andExpect(jsonPath("$.subfolders").isArray()).andExpect(jsonPath("$.parent_folder").isString());
	}

	@Test
	void deleteById() throws Exception {
		doNothing().when(folderService).deleteById(Mockito.any(UUID.class));

		mockMvc.perform(delete(BASE_URL + "/{id}", UUID.randomUUID())).andExpect(status().isOk());
	}

	@Test
	void moveFilesToFolder() throws Exception {
		BDDMockito.given(folderService.moveFilesToFolder(Mockito.any(UUID.class), Mockito.anyList()))
				.willReturn(rootFolderDTO);

		mockMvc.perform(post(BASE_URL + "/move/{id}", UUID.randomUUID()).content(Utils.stringify(new ArrayList<>()))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

}
