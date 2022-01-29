package com.example.dms.api.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
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

import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsType;
import com.example.dms.domain.DmsUser;
import com.example.dms.services.DocumentService;
import com.example.dms.utils.Utils;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	DocumentService documentService;

	DmsUser validUser;
	DmsDocument validDocument;
	DmsType type;
	NewDocumentDTO newDocumentDTO;
	DmsDocumentDTO validDocumentDTO;
	
	private static final String BASE_URL = "/api/v1/documents";

	@BeforeEach
	void setUp() {
		type = DmsType.builder().typeName("tajni").build();
		validUser = DmsUser.builder().username("dcrncic").password("12345").firstName("Darjan").lastName("Crnčić")
				.email("darjan.crncic@gmail.com").build();
		validUser.setId(UUID.randomUUID());

		validDocument = DmsDocument.builder().objectName("testni").creator(validUser)
				.description("testni dokument za test").keywords(Arrays.asList(new String[] { "prvi", "drugi" }))
				.imutable(false).type(type).build();

		newDocumentDTO = NewDocumentDTO.builder().objectName("testni").description("testni dokument za test")
				.keywords(Arrays.asList(new String[] { "prvi", "drugi" })).type(type).build();
		validDocumentDTO = DmsDocumentDTO.builder().objectName("testni").description("testni dokument za test")
				.keywords(Arrays.asList(new String[] { "prvi", "drugi" })).type(type).build();
	}

	@Test
	void createNewDocumentTest() throws Exception {
		BDDMockito.given(documentService.createNewDocument(Mockito.any(NewDocumentDTO.class))).willReturn(validDocumentDTO);

		mockMvc.perform(post(BASE_URL).content(Utils.stringify(newDocumentDTO)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
				.andExpect(jsonPath("$.object_name", is(validDocument.getObjectName())))
				.andExpect(jsonPath("$.description", is(validDocument.getDescription())))
				.andExpect(jsonPath("$.keywords").isArray());
	}
	
	@Test
	void deleteDocumentTest() throws Exception {
		doNothing().when(documentService).deleteById(Mockito.any(UUID.class));
		
		mockMvc.perform(delete(BASE_URL + "/{id}", UUID.randomUUID())).andExpect(status().isOk());
	}
	
	@Test 
	void getAllDocumentsTest() throws Exception {
		BDDMockito.given(documentService.findAll()).willReturn(Arrays.asList(validDocumentDTO)); 
		
		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
	}
}
