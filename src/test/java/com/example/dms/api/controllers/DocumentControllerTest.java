package com.example.dms.api.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.api.dtos.document.DocumentDTO;
import com.example.dms.api.mappers.DocumentMapper;
import com.example.dms.domain.Document;
import com.example.dms.domain.User;
import com.example.dms.services.DocumentService;
import com.example.dms.services.UserService;

@SpringBootTest
class DocumentControllerTest {

	@Autowired
	DocumentMapper documentMapper;

	@Autowired
	DocumentService documentService;

	@Autowired
	UserService userService;
	
	@Test
	@Transactional
	void testDocumentToDocumentDTOMapping() {
		User user = userService.save(new User("testuser", "12345", "Darjan", "Crnčić", "test.user@gmail.com"));
		
		Document doc1 = new Document(user, "test1");
		DocumentDTO docDTO = documentMapper.documentToDocumentDTO(doc1);

		assertEquals(doc1.getObjectName(), docDTO.getObjectName());
	}

	@Test
	@Transactional
	void saveNewDocumentTest() {
		User user = userService.save(new User("testuser", "12345", "Darjan", "Crnčić", "test.user@gmail.com"));

		Document newDocument = documentService.save(
				Document.builder().creator(user).objectName("TestTest").description("Ovo je test u testu").build());
		Document foundDocument = documentService.findById(newDocument.getId());

		assertEquals(newDocument.getObjectName(), foundDocument.getObjectName());
		assertEquals(foundDocument.getId(), newDocument.getId());
		
	}
}
