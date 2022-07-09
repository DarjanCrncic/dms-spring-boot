package com.example.dms.services.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

import javax.sql.DataSource;

import org.h2.tools.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.api.dtos.folder.DmsFolderDTO;
import com.example.dms.api.mappers.FolderMapper;
import com.example.dms.domain.DmsFolder;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.DocumentService;
import com.example.dms.services.FolderService;
import com.example.dms.utils.exceptions.DmsNotFoundException;

@SpringBootTest
@ContextConfiguration
@WithMockUser(username = "user", authorities = { "CREATE_PRIVILEGE", "DELETE_PRIVILEGE", "READ_PRIVILEGE", "WRITE_PRIVILEGE" })
@TestInstance(Lifecycle.PER_CLASS) // DEBUGGING WITH H2
class FolderServiceIT {

	@Autowired
	FolderMapper folderMapper;

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

//  DEBUGGING WITH H2 
	@Autowired
	DataSource dataSource;

	@BeforeAll
	public void initTest() throws SQLException {
		Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
	}

	DmsUser user;
	DmsFolderDTO folder;
	DmsFolder folderObject;
	DmsFolderDTO subFolder;
	DmsDocumentDTO newDocument;
	DmsDocumentDTO documentWithPermissions;

	@BeforeEach
	void setUp() {
		folder = folderService.createFolder("/test", "user");
		subFolder = folderService.createFolder("/test/inside", "user");
		folderObject = folderRepository.findById(folder.getId()).orElse(null);
		newDocument = documentService.createDocument(NewDocumentDTO.builder().objectName("TestTest")
				.description("Ovo je test u testu").username("user").parentFolder(folderObject.getPath()).build());

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
	void checkRootFolderInitialized() {
		assertTrue(folderRepository.findByPath("/").isPresent());
	}

	@Test
	@Transactional
	@DisplayName("Test creation of folder and subfolder.")
	void createNewFolderTest() {
		assertEquals(1, folderObject.getSubfolders().size());
	}

	@Test
	@DisplayName("Test deleting folder and documents within.")
	void deleteFolderTest() {
		DmsDocumentDTO newDocument = documentService.createDocument(NewDocumentDTO.builder().username("user")
				.objectName("TestTest").description("Ovo je test u testu").parentFolder(folder.getPath()).build());

		assertEquals(folder.getPath(), newDocument.getParentFolder());

		folderService.deleteById(folder.getId());

		UUID subFolderId = subFolder.getId();
		UUID newDocumentId = newDocument.getId();

		assertThrows(DmsNotFoundException.class, () -> folderService.findById(subFolderId));
		assertThrows(DmsNotFoundException.class, () -> documentService.findById(newDocumentId));
	}

	@Test
	@DisplayName("Test deletion of subfolder.")
	void deleteChildrenTest() {
		folderService.deleteById(subFolder.getId());
		UUID subFolderId = subFolder.getId();

		assertTrue(folderRepository.findById(subFolderId).isEmpty());
	}

	@Test
	@DisplayName("Test modifying folder path.")
	void modifiyFolderTest() {
		folderService.updateFolder(folder.getId(), "/renamed");
		subFolder = folderService.findById(subFolder.getId());
		assertEquals("/renamed", subFolder.getParentFolder());
	}

	@Test
	@DisplayName("Test moving documents from folder to folder.")
	void moveDocumentToDifferentFolder() {
		folder = folderService.findById(folder.getId());
		//assertEquals(1, folder.getDocuments().size());

		subFolder = folderService.moveFilesToFolder(subFolder.getId(), Arrays.asList(newDocument.getId()));
		folder = folderService.findById(folder.getId());

		//assertEquals(0, folder.getDocuments().size());
		//assertEquals(1, subFolder.getDocuments().size());
	}

	@Test
	@DisplayName("Test moving documents from folder to folder with security.")
	void moveDocumentToDifferentFolderSecurityTest() {
		DmsFolderDTO subFolderPerm = folderService.createFolder("/test/perm", "user");
		documentWithPermissions = documentService.createDocument(NewDocumentDTO.builder().objectName("Permissions")
				.description("Test with permission").username("user").build());
		subFolderPerm = folderService.moveFilesToFolder(subFolderPerm.getId(),
				Arrays.asList(documentWithPermissions.getId()));

		folder = folderService.findById(folder.getId());

		//assertEquals(1, subFolderPerm.getDocuments().size());
	}

	@Test
	@DisplayName("Test delete folder with security.")
	void deleteFolderWithDocuments() {
		assertDoesNotThrow(() -> folderService.delete(folderObject));
	}

}
