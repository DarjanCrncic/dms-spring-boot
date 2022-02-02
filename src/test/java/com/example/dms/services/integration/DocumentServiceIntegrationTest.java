package com.example.dms.services.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.api.mappers.DocumentMapper;
import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsType;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.repositories.TypeRepository;
import com.example.dms.repositories.UserRepository;
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
	
	@Autowired
	DocumentRepository documentRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	TypeRepository typeRepository;

	DmsDocumentDTO newDocument;
	DmsDocumentDTO newVersion;
	DmsDocumentDTO updatedDocument;
	DmsType type;
	String typeName = "testni-tip";
	
	@BeforeEach
	void setUp() {
		type = typeRepository.save(DmsType.builder().typeName(typeName).build());
		newDocument = documentService.createNewDocument(
				NewDocumentDTO.builder().objectName("TestTest").description("Ovo je test u testu").typeName(typeName).build());
	}

	@AfterEach
	void cleanUp() {
		if (newDocument != null && documentRepository.findById(newDocument.getId()).isPresent())
			documentService.deleteById(newDocument.getId());
		if (newVersion != null && documentRepository.findById(newVersion.getId()).isPresent())
			documentService.deleteById(newVersion.getId());
		if (updatedDocument != null && documentRepository.findById(updatedDocument.getId()).isPresent()) 
			documentService.deleteById(updatedDocument.getId());
		if (typeRepository.findByTypeName(typeName).isPresent()) 
			typeRepository.delete(type);
	}

	@Test
	void saveNewDocumentTest() {
		DmsDocument foundDocument = documentRepository.findById(newDocument.getId()).orElse(null);

		assertEquals(newDocument.getObjectName(), foundDocument.getObjectName());
		assertEquals(newDocument.getId(), foundDocument.getId());
		assertSame(newDocument.getCreator().getUsername(), foundDocument.getCreator().getUsername());
	}
	
	@Test
	@Transactional
	void testTypeBinding() {
		type = typeRepository.findByTypeName(typeName).orElse(null);
		
		assertEquals(type.getTypeName(), newDocument.getType().getTypeName());
		assertEquals(type.getTypeName(), typeName);
		assertThat(type.getDocuments()).hasSize(1);
	}

	@Test
	@Transactional
	void saveNewDocumentPersistenceTest() {
		DmsUser creator = userRepository.findById(newDocument.getCreator().getId()).orElse(null);
		assertEquals(1, creator.getDocuments().size());
	}

	@Test
	void testVersioning() {
		newVersion = documentService.createNewVersion(newDocument.getId());
		newDocument = documentService.findById(newDocument.getId());

		assertEquals(2, newVersion.getVersion());
		assertEquals(newDocument.getObjectName(), newVersion.getObjectName());
		assertTrue(newDocument.isImutable());
		assertFalse(newVersion.isImutable());

		assertEquals(newDocument.getId(), newDocument.getRootId());
		assertEquals(newDocument.getId(), newVersion.getRootId());

		assertThrows(BadRequestException.class, () -> documentService.createNewVersion(newDocument.getId()));

		assertEquals(2, documentService.getAllVersions(newVersion.getId()).size());
	}

	@Test
	void testDocumentPut() {
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").description("updated")
				.keywords(Arrays.asList(new String[] { "foo", "bar" })).build();
		updatedDocument = documentService.updateDocument(newDocument.getId(), modifyDTO, false);

		assertEquals(modifyDTO.getObjectName(), updatedDocument.getObjectName());
		assertEquals(modifyDTO.getDescription(), updatedDocument.getDescription());
		assertEquals(modifyDTO.getKeywords().get(0), updatedDocument.getKeywords().get(0));
		assertEquals(newDocument.getRootId(), updatedDocument.getRootId());
	}
	
	@Test
	void testDocumentPatch() {
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		updatedDocument = documentService.updateDocument(newDocument.getId(), modifyDTO, true);
		
		assertEquals(modifyDTO.getObjectName(), updatedDocument.getObjectName());
		assertNull(modifyDTO.getDescription());
		assertNull(modifyDTO.getKeywords());
		assertEquals(newDocument.getRootId(), updatedDocument.getRootId());
	}
}
