package com.example.dms.services.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.api.dtos.document.DocumentDTO;
import com.example.dms.api.dtos.folder.DmsFolderDTO;
import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.DocumentService;
import com.example.dms.services.FolderService;
import com.example.dms.utils.exceptions.NotFoundException;

@SpringBootTest
class FolderServiceIntegrationTest {

	@Autowired
	FolderRepository folderRepository;

	@Autowired
	FolderService folderService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	DocumentService documentService;

	DmsUser user;
	DmsFolderDTO folder;
	DmsFolderDTO subFolder;

	@BeforeEach
	void setUp() {
		user = userRepository.save(DmsUser.builder().username("dcrncictest").password("12345").firstName("Darjan")
				.lastName("Crnčić").email("darjan.crncic.test@gmail.com").build());
		folder = folderService.createNewFolder("/test");
		subFolder = folderService.createNewFolder("/test/inside");
	}

	@AfterEach
	void cleanUp() {
		if (user != null && userRepository.findById(user.getId()).isPresent())
			userRepository.deleteById(user.getId());
		if (subFolder != null && folderRepository.findById(subFolder.getId()).isPresent())
			folderService.deleteById(subFolder.getId());
		if (folder != null && folderRepository.findById(folder.getId()).isPresent())
			folderService.deleteById(folder.getId());
	}

	@Test
	void checkRootFolderInitialized() {
		assertTrue(folderRepository.findByPath("/").isPresent());
	}

	@Test
	@Transactional
	void createNewFolderTest() {
		assertEquals(0, folder.getSubfolders().size());
	}

	@Test
	void deleteFolderTest() {
		DocumentDTO newDocument = documentService
				.save(DmsDocument.builder().creator(user).objectName("TestTest").description("Ovo je test u testu")
						.parentFolder(folderRepository.findById(folder.getId()).orElse(null)).build());

		assertEquals(folder.getId(), newDocument.getParentFolder().getId());

		folderService.deleteById(folder.getId());
		
		UUID subFolderId = subFolder.getId();
		UUID newDocumentId = newDocument.getId();
		
		assertThrows(NotFoundException.class, () -> folderService.findById(subFolderId));
		assertThrows(NotFoundException.class, () -> documentService.findById(newDocumentId));
	}

	@Test
	void deleteChildrenTest() {
		DocumentDTO newDocument = documentService.save(DmsDocument.builder().creator(user).objectName("TestTest")
				.parentFolder(folderRepository.findById(folder.getId()).orElse(null)).build());

		folderService.deleteById(subFolder.getId());
		documentService.deleteById(newDocument.getId());

		assertDoesNotThrow(() -> folderService.findById(folder.getId()));
	}

	@Test
	void modifiyFolderTest() {
		folderService.updateFolder(folder.getId(), "/renamed");
		subFolder = folderService.findById(subFolder.getId());
		assertEquals("/renamed", subFolder.getParentFolder().getPath());
	}
}
