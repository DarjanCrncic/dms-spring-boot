package com.example.dms.services.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.dms.api.dtos.document.DocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.api.mappers.DocumentMapper;
import com.example.dms.domain.DmsDocument;
import com.example.dms.services.DocumentService;
import com.example.dms.services.UserService;
import com.example.dms.utils.exceptions.BadRequestException;

@SpringBootTest
class DocumentServiceIntegrationTest {

	@Autowired
	DocumentMapper documentMapper;

	@Autowired
	DocumentService documentService;

	@Autowired
	UserService userService;
	
	DmsDocument newDocument;
	DmsDocument newVersion;
	
	@BeforeEach
	void setUp() {
		newDocument = documentService.createNewDocument(
				NewDocumentDTO.builder().objectName("TestTest").description("Ovo je test u testu").build());
	}
	
	@AfterEach
	void cleanUp() {
		if (newDocument != null) documentService.delete(newDocument);
		if (newVersion != null) documentService.delete(newVersion);
	}
	
	@Test
	void testDocumentToDocumentDTOMapping() {
		DocumentDTO docDTO = documentMapper.documentToDocumentDTO(newDocument);

		assertEquals(newDocument.getObjectName(), docDTO.getObjectName());
		assertEquals(newDocument.getCreator().getId(), docDTO.getCreator().getId());
		
		documentService.delete(newDocument);
	}

	@Test
	void saveNewDocumentTest() {
		DmsDocument foundDocument = documentService.findById(newDocument.getId());

		assertEquals(newDocument.getObjectName(), foundDocument.getObjectName());
		assertEquals(newDocument.getId(), foundDocument.getId());
		assertSame(newDocument.getCreator().getUsername(), foundDocument.getCreator().getUsername());
	}
	
	@Test
	void testVersioning() {
		newVersion = documentService.createNewVersion(newDocument.getId());
		newDocument = documentService.refresh(newDocument);
		
		assertEquals(2, newVersion.getVersion());
		assertEquals(newDocument.getObjectName(), newVersion.getObjectName());
		assertTrue(newDocument.isImutable());
		assertFalse(newVersion.isImutable());
		
		assertEquals(newDocument.getId(), newDocument.getRootId());
		assertEquals(newDocument.getId(), newVersion.getRootId());
		
		assertThrows(BadRequestException.class, () -> documentService.createNewVersion(newDocument.getId()));
		
		assertEquals(2, documentService.getAllVersions(newVersion.getId()).size());
	}
}
