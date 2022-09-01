package com.example.dms.services.integration;

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

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
	DmsFolder folderObject, root;
	DmsFolderDTO subFolder;
	DmsDocumentDTO newDocument;
	DmsDocumentDTO documentWithPermissions;

	@BeforeEach
	void setUp() {
		root = folderRepository.findByName("/").orElse(null);
		folder = folderService.createFolder("test", "user", root.getId());
		subFolder = folderService.createFolder("inside", "user", folder.getId());
		folderObject = folderRepository.findById(folder.getId()).orElse(null);
		newDocument = documentService.createDocument(NewDocumentDTO.builder().objectName("TestTest")
				.description("Ovo je test u testu").username("user").parentFolderId(folderObject.getId()).type("document").build());

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
		assertTrue(folderRepository.findByName("/").isPresent());
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
				.objectName("TestTest").description("Ovo je test u testu").parentFolderId(folder.getId()).type("document").build());

		assertEquals(folder.getId(), newDocument.getParentFolderId());

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
		DmsFolderDTO renamed = folderService.updateFolder(folder.getId(), "renamed");
		assertEquals("renamed", renamed.getName());
	}

//	@Test
//	@DisplayName("Test moving documents from folder to folder.")
//	@Transactional
//	void moveDocumentToDifferentFolder() {
//		folderObject = folderRepository.findById(folder.getId()).orElse(null);
//		assertEquals(1, folderObject.getDocuments().size());
//
//		subFolder = folderService.moveFilesToFolder(subFolder.getId(), Arrays.asList(newDocument.getId()));
//		DmsFolder subFolderObject = folderRepository.findById(subFolder.getId()).orElse(null);
//		folderObject = folderRepository.findById(folder.getId()).orElse(null);
//
//		assertEquals(0, folderObject.getDocuments().size());
//		assertEquals(1, subFolderObject.getDocuments().size());
//	}

//	@Test
//	@DisplayName("Test moving documents from folder to folder with security.")
//	@Transactional
//	void moveDocumentToDifferentFolderSecurityTest() {
//		DmsFolderDTO subFolderPerm = folderService.createFolder("/test/perm", "user");
//		documentWithPermissions = documentService.createDocument(NewDocumentDTO.builder().objectName("Permissions")
//				.description("Test with permission").username("user").type("document").build());
//		subFolderPerm = folderService.moveFilesToFolder(subFolderPerm.getId(),
//				Arrays.asList(documentWithPermissions.getId()));
//
//		folder = folderService.findById(folder.getId());
//		DmsFolder subFolderObject = folderRepository.findById(subFolderPerm.getId()).orElse(null);
//		assertEquals(1, subFolderObject.getDocuments().size());
//	}

	@Test
	@DisplayName("Test delete folder with security.")
	void deleteFolderWithDocuments() {
		assertDoesNotThrow(() -> folderService.deleteById(folderObject.getId()));
	}

}
