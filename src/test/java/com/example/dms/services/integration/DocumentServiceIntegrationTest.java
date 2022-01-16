package com.example.dms.services.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.api.dtos.document.DocumentDTO;
import com.example.dms.api.mappers.DocumentMapper;
import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsUser;
import com.example.dms.services.DocumentService;
import com.example.dms.services.UserService;

@SpringBootTest
class DocumentServiceIntegrationTest {

	@Autowired
	DocumentMapper documentMapper;

	@Autowired
	DocumentService documentService;

	@Autowired
	UserService userService;
	
	@Test
	@Transactional
	void testDocumentToDocumentDTOMapping() {
		DmsUser user = userService.save(new DmsUser("testuser", "12345", "Darjan", "Crnčić", "test.user@gmail.com"));
		
		DmsDocument newDocument = new DmsDocument(user, "test1");
		DocumentDTO docDTO = documentMapper.documentToDocumentDTO(newDocument);

		assertEquals(newDocument.getObjectName(), docDTO.getObjectName());
		assertEquals(newDocument.getCreator().getId(), docDTO.getCreator().getId());
	}

	@Test
	@Transactional
	void saveNewDocumentTest() {
		DmsUser user = userService.save(new DmsUser("testuser", "12345", "Darjan", "Crnčić", "test.user@gmail.com"));

		DmsDocument newDocument = documentService.save(
				DmsDocument.builder().creator(user).objectName("TestTest").description("Ovo je test u testu").build());
		DmsDocument foundDocument = documentService.findById(newDocument.getId());

		assertEquals(newDocument.getObjectName(), foundDocument.getObjectName());
		assertEquals(newDocument.getId(), foundDocument.getId());
		assertSame(newDocument.getCreator(), foundDocument.getCreator());
		
	}
}
