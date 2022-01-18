package com.example.dms.services.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsFolder;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.services.DocumentService;
import com.example.dms.services.FolderService;
import com.example.dms.services.UserService;
import com.example.dms.utils.exceptions.NotFoundException;

@SpringBootTest
class FolderServiceIntegrationTest {
	
	@Autowired
	FolderRepository folderRepository;
	
	@Autowired
	FolderService folderService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	DocumentService documentService;
	
	DmsUser user;
	DmsFolder folder;
	DmsFolder subFolder;
	
	@BeforeEach 
	void setUp() {
		user = userService.saveNewUser(new NewUserDTO("testuser", "12345", "Darjan", "Crnčić", "test.user@gmail.com"));
		folder = folderService.createNewFolder("/test");
		subFolder = folderService.createNewFolder("/test/inside");
	}
	
	@AfterEach
	void cleanUp() {
		userService.delete(user);
		folderService.delete(subFolder);
		folderService.delete(folder);
	}
	
	@Test
	void checkRootFolderInitialized() {
		assertTrue(folderRepository.findByPath("/").isPresent());
	}

	@Test
	@Transactional
	void createNewFolderTest() {
		assertEquals(0, folderService.findByPath("/test").getSubfolders().size());
	}
	
	@Test
	void deleteFolderTest() {
		DmsDocument newDocument = documentService.save(
				DmsDocument.builder().creator(user).objectName("TestTest").description("Ovo je test u testu").parentFolder(folder).build());
		
		assertEquals(folder, newDocument.getParentFolder());
		
		folderService.deleteById(folder.getId());
		assertThrows(NotFoundException.class, () -> folderService.findById(subFolder.getId()));
		assertThrows(NotFoundException.class, () -> documentService.findById(newDocument.getId()));
	}

	@Test
	void deleteChildrenTest() {	
		DmsDocument newDocument = documentService.save(
				DmsDocument.builder().creator(user).objectName("TestTest").description("Ovo je test u testu").parentFolder(folder).build());
		
		folderService.delete(subFolder);
		documentService.delete(newDocument);

		assertDoesNotThrow(() -> folderService.findById(folder.getId()));
	}
	
	@Test
	void modifiyFolderTest() {
		folderService.updateFolder(folder.getId(), "/renamed");
		subFolder = folderService.findById(subFolder.getId());
		assertEquals("/renamed", subFolder.getParentFolder().getPath());
	}
}
