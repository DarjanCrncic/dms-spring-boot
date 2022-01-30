package com.example.dms.api.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.dms.api.dtos.folder.DmsFolderDTO;
import com.example.dms.api.dtos.folder.NewFolderDTO;
import com.example.dms.domain.DmsUser;
import com.example.dms.services.FolderService;
import com.example.dms.utils.Utils;

@WebMvcTest(FolderController.class)
@AutoConfigureRestDocs(outputDir = "target/snippets")
class FolderControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	FolderService folderService;

	DmsUser validUser;
	DmsFolderDTO rootFolderDTO;
	DmsFolderDTO validFolderDTO;
	List<DmsFolderDTO> folderList;
	
	private static final String BASE_URL = "/api/v1/folders";

	@BeforeEach
	void setUp() {
		validUser = DmsUser.builder().username("dcrncic").password("12345").firstName("Darjan").lastName("Crnčić").email("darjan.crncic@gmail.com").build();
		validUser.setId(UUID.randomUUID());

		rootFolderDTO = DmsFolderDTO.builder().path("/").build();
		validFolderDTO = DmsFolderDTO.builder().path("/test").parentFolder(DmsFolderDTO.builder().path("/").build()).build();

		folderList = new ArrayList<DmsFolderDTO>();
		folderList.add(rootFolderDTO);
		folderList.add(validFolderDTO);
	}

	@Test
	void testGetAllFolders() throws Exception {
		BDDMockito.given(folderService.findAll()).willReturn(folderList);

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$").isArray()).andDo(document("folders"));;
	}

	@Test
	void testFindById() throws Exception {
		BDDMockito.given(folderService.findById(Mockito.any(UUID.class))).willReturn(rootFolderDTO);

		mockMvc.perform(get(BASE_URL + "/{id}", UUID.randomUUID()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.path", is(rootFolderDTO.getPath())))
				.andExpect(jsonPath("$.subfolders").isArray())
				.andExpect(jsonPath("$.documents").isArray());

	}

	@Test
	void testFolderSearchByPath() throws Exception {
		BDDMockito.given(folderService.findByPath(Mockito.anyString())).willReturn(rootFolderDTO);

		mockMvc.perform(get(BASE_URL + "/search").param("path", Mockito.anyString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.subfolders").isArray())
				.andExpect(jsonPath("$.documents").isArray());
	}

	@Test
	void testSaveNewFolder() throws Exception {
		BDDMockito.given(folderService.createNewFolder(Mockito.anyString())).willReturn(validFolderDTO);
		mockMvc.perform(post(BASE_URL).content(Utils.stringify(new NewFolderDTO("/test"))).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.path", is(validFolderDTO.getPath())))
				.andExpect(jsonPath("$.subfolders").isArray())
				.andExpect(jsonPath("$.documents").isArray());;
	}
	
	@Test
	void testUpdateFolder() throws Exception {
		BDDMockito.given(folderService.updateFolder(Mockito.any(UUID.class), Mockito.anyString())).willReturn(validFolderDTO);

		mockMvc.perform(put(BASE_URL + "/{id}", UUID.randomUUID()).content(Utils.stringify(new NewFolderDTO("/test"))).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.path", is(validFolderDTO.getPath())))
				.andExpect(jsonPath("$.subfolders").isArray())
				.andExpect(jsonPath("$.documents").isArray());;
	}

	@Test
	void deleteById() throws Exception {
		doNothing().when(folderService).deleteById(Mockito.any(UUID.class));

		mockMvc.perform(delete(BASE_URL + "/{id}", UUID.randomUUID())).andExpect(status().isOk());
	}
	
	@Test
	void moveFilesToFolder() throws Exception {
		BDDMockito.given(folderService.moveFilesToFolder(Mockito.any(UUID.class), Mockito.anyList())).willReturn(rootFolderDTO);
		
		mockMvc.perform(post(BASE_URL + "/move/{id}", UUID.randomUUID()).content(Utils.stringify(new ArrayList<>())).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

}
