package com.example.dms.services.integration;

import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.api.dtos.folder.DmsFolderDTO;
import com.example.dms.api.dtos.folder.NewFolderDTO;
import com.example.dms.domain.DmsFolder;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.DocumentService;
import com.example.dms.services.FolderService;
import com.example.dms.utils.exceptions.DmsNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ContextConfiguration
@WithUserDetails(value = "admin", userDetailsServiceBeanName = "dmsUserDetailsService")
class FolderServiceIT {

	@Autowired
	FolderRepository folderRepository;

	@Autowired
	FolderService folderService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	DocumentService documentService;

	@Autowired
	DocumentRepository documentRepository;

	DmsUser user;
	DmsFolderDTO folder;
	DmsFolder folderObject, root;
	DmsFolderDTO subFolder;
	DmsDocumentDTO newDocument;
	DmsDocumentDTO documentWithPermissions;

	@BeforeEach
	void setUp() {
		root = folderRepository.findByName("/").orElse(null);
		assert root != null;

		folder = folderService.createFolder(NewFolderDTO.builder().name("test").parentFolderId(root.getId()).rootFolder(true).build());
		subFolder = folderService.createFolder(NewFolderDTO.builder().name("inside").parentFolderId(folder.getId()).rootFolder(false).build());
		folderObject = folderRepository.findById(folder.getId()).orElse(null);
		newDocument = documentService.createDocument(NewDocumentDTO.builder().objectName("TestTest")
				.description("Ovo je test u testu").parentFolderId(folderObject.getId()).type("document").build());

	}

	@AfterEach
	void cleanUp() {
		if (newDocument != null && documentRepository.existsById(newDocument.getId()))
			documentRepository.deleteById(newDocument.getId());
		if (documentWithPermissions != null && documentRepository.existsById(documentWithPermissions.getId()))
			documentRepository.deleteById(documentWithPermissions.getId());
		if (user != null && userRepository.existsById(user.getId()))
			userRepository.deleteById(user.getId());
		if (subFolder != null && folderRepository.existsById(subFolder.getId()))
			folderRepository.deleteById(subFolder.getId());
		if (folder != null && folderRepository.existsById(folder.getId()))
			folderRepository.deleteById(folder.getId());
	}

	@Test
	@DisplayName("Check if root folder is initialized.")
	void checkRootFolderInitialized() {
		assertTrue(folderRepository.findByName("/").isPresent());
	}

	@Test
	@DisplayName("Test creation of folder and subfolder.")
	void createNewFolderTest() {
		assertEquals(1, documentRepository.findByParentFolderId(folderObject.getId()).size());
	}

	@Test
	@DisplayName("Test deleting folder and documents within.")
	void deleteFolderTest() {
		DmsDocumentDTO newDocument = documentService.createDocument(NewDocumentDTO.builder()
				.objectName("TestTest").description("Ovo je test u testu").parentFolderId(folder.getId()).type("document").build());

		assertEquals(folder.getId(), newDocument.getParentFolderId());

		folderService.deleteById(folder.getId());

		Integer subFolderId = subFolder.getId();
		Integer newDocumentId = newDocument.getId();

		assertThrows(DmsNotFoundException.class, () -> folderService.findById(subFolderId));
		assertThrows(DmsNotFoundException.class, () -> documentService.findById(newDocumentId));
	}

	@Test
	@DisplayName("Test deletion of subfolder.")
	void deleteChildrenTest() {
		folderService.deleteById(subFolder.getId());
		Integer subFolderId = subFolder.getId();

		assertTrue(folderRepository.findById(subFolderId).isEmpty());
	}

	@Test
	@DisplayName("Test modifying folder path.")
	void modifyFolderTest() {
		DmsFolderDTO renamed = folderService.updateFolder(folder.getId(), "renamed");
		assertEquals("renamed", renamed.getName());
	}

	@Test
	@DisplayName("Test moving documents from folder to folder.")
	void moveDocumentToDifferentFolder() {
		folderObject = folderRepository.findById(folder.getId()).orElse(null);
		assert folderObject != null;
		assertEquals(1, documentRepository.findByParentFolderId(folderObject.getId()).size());

		subFolder = folderService.moveFilesToFolder(subFolder.getId(), List.of(newDocument.getId()));

		DmsFolder subFolderObject = folderRepository.findById(subFolder.getId()).orElse(null);
		assert subFolderObject != null;
		folderObject = folderRepository.findById(folder.getId()).orElse(null);

		assert folderObject != null;
		assertEquals(0, documentRepository.findByParentFolderId(folderObject.getId()).size());
		assertEquals(1, documentRepository.findByParentFolderId(subFolderObject.getId()).size());
	}

	@Test
	@DisplayName("Test delete folder with security.")
	void deleteFolderWithDocuments() {
		assertDoesNotThrow(() -> folderService.deleteById(folderObject.getId()));
	}

}
