package com.example.dms.services.integration;

import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsFolder;
import com.example.dms.domain.DmsType;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.repositories.TypeRepository;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.DocumentService;
import com.example.dms.utils.exceptions.BadRequestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration
@WithUserDetails(value = "admin", userDetailsServiceBeanName = "dmsUserDetailsService")
class DocumentServiceIT {

	@Autowired
	DocumentService documentService;

	@Autowired
	FolderRepository folderRepository;

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
	DmsFolder folder;
	String typeName = "testni-tip";

	@BeforeEach
	void setUp() {
		type = typeRepository.save(DmsType.builder().typeName(typeName).build());
		folder = folderRepository.save(DmsFolder.builder().name("test").build());
		newDocument = documentService.createDocument(
				NewDocumentDTO.builder().objectName("TestTest").description("Ovo je test u testu").type(typeName).parentFolderId(folder.getId()).build());
	}

	@AfterEach
	void cleanUp() {
		if (newDocument != null && documentRepository.existsById(newDocument.getId()))
			documentRepository.deleteById(newDocument.getId());
		if (newVersion != null && documentRepository.existsById(newVersion.getId()))
			documentRepository.deleteById(newVersion.getId());
		if (updatedDocument != null && documentRepository.existsById(updatedDocument.getId()))
			documentRepository.deleteById(updatedDocument.getId());
		if (type != null && typeRepository.existsById(type.getId()))
			typeRepository.delete(type);
	}

	@Test
	@DisplayName("Test saving of a new document.")
	void saveNewDocumentTest() {
		DmsDocument foundDocument = documentRepository.findById(newDocument.getId()).orElse(null);
		assert foundDocument != null;

		assertEquals(newDocument.getObjectName(), foundDocument.getObjectName());
		assertEquals(newDocument.getId(), foundDocument.getId());
		assertSame(newDocument.getCreator().getUsername(), foundDocument.getCreator().getUsername());
	}

	@Test
	@DisplayName("Test binding of document to type.")
	@Transactional
	void testTypeBinding() {
		type = typeRepository.findByTypeName(typeName).orElse(null);
		assert type != null;

		assertEquals(type.getTypeName(), newDocument.getType());
		assertEquals(type.getTypeName(), typeName);
		assertThat(type.getDocuments()).hasSize(1);
	}

	@Test
	@DisplayName("Test if the document relation is persisted for the creator user.")
	@Transactional
	void saveNewDocumentPersistenceTest() {
		DmsUser creator = userRepository.findById(newDocument.getCreator().getId()).orElse(null);
		assert creator != null;

		assertEquals(1, creator.getDocuments().size());
	}

	@Test
	@DisplayName("Test document versioning and previous version immutability.")
	void testVersioning() {
		newVersion = documentService.createNewVersion(newDocument.getId());
		newDocument = documentService.findById(newDocument.getId());

		assertEquals("2", newVersion.getVersion());
		assertEquals(newDocument.getObjectName(), newVersion.getObjectName());
		assertTrue(newDocument.isImmutable());
		assertFalse(newVersion.isImmutable());

		assertEquals(newDocument.getId(), newDocument.getRootId());
		assertEquals(newDocument.getId(), newVersion.getRootId());

		Integer docId = newDocument.getId();
		assertThrows(BadRequestException.class, () -> documentService.createNewVersion(docId));
		assertEquals(2, documentService.getAllVersions(newVersion.getRootId()).size());
	}

	@Test
	@DisplayName("Test modifying document with put HTTP request.")
	void testDocumentPut() {
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").description("updated")
				.keywords(Arrays.asList("foo", "bar")).build();
		updatedDocument = documentService.updateDocument(newDocument.getId(), modifyDTO, false);

		assertEquals(modifyDTO.getObjectName(), updatedDocument.getObjectName());
		assertEquals(modifyDTO.getDescription(), updatedDocument.getDescription());
		assertEquals(modifyDTO.getKeywords().get(0), updatedDocument.getKeywords().get(0));
		assertEquals(newDocument.getRootId(), updatedDocument.getRootId());
	}

	@Test
	@DisplayName("Test modifying document with patch HTTP request.")
	void testDocumentPatch() {
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		updatedDocument = documentService.updateDocument(newDocument.getId(), modifyDTO, true);

		assertEquals(modifyDTO.getObjectName(), updatedDocument.getObjectName());
		assertNull(modifyDTO.getDescription());
		assertNull(modifyDTO.getKeywords());
		assertEquals(newDocument.getRootId(), updatedDocument.getRootId());
	}

	@Test
	void testEquals() {
		DmsDocument doc1 = documentRepository.findById(newDocument.getId()).orElse(null);
		DmsDocument doc2 = documentRepository.findById(newDocument.getId()).orElse(null);

		assert doc1 != null;
		assert doc2 != null;
		assertEquals(doc1, doc2);
	}
}
