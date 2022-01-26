package com.example.dms.api.controllers;

import static org.hamcrest.CoreMatchers.is;
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

import com.example.dms.api.dtos.document.DocumentDTO;
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
	DocumentDTO validDocumentDTO;

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
		validDocumentDTO = DocumentDTO.builder().objectName("testni").description("testni dokument za test")
				.keywords(Arrays.asList(new String[] { "prvi", "drugi" })).type(type).build();
	}

	@Test
	public void createNewDocumentTest() throws Exception {
		BDDMockito.given(documentService.createNewDocument(Mockito.any(NewDocumentDTO.class))).willReturn(validDocumentDTO);

		mockMvc.perform(post("/api/v1/documents/").content(Utils.stringify(newDocumentDTO)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
				.andExpect(jsonPath("$.object_name", is(validDocument.getObjectName())))
				.andExpect(jsonPath("$.description", is(validDocument.getDescription())))
				.andExpect(jsonPath("$.keywords").isArray());
	}
}
