package com.example.dms.services.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.folder.DmsFolderDTO;
import com.example.dms.api.mappers.FolderMapper;
import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsFolder;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.DocumentService;
import com.example.dms.services.FolderService;
import com.example.dms.utils.exceptions.NotFoundException;

@SpringBootTest
class FolderServiceIntegrationTest {
	
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

	DmsUser user;
	DmsFolderDTO folder;
	DmsFolder folderObject;
	DmsFolderDTO subFolder;
	DmsDocument newDocument;

	@BeforeEach
	void setUp() {
		user = userRepository.save(DmsUser.builder().username("dcrncictest").password("12345").firstName("Darjan")
				.lastName("Crnčić").email("darjan.crncic.test@gmail.com").build());
		folder = folderService.createNewFolder("/test");
		subFolder = folderService.createNewFolder("/test/inside");
		folderObject = folderRepository.findById(folder.getId()).orElse(null);
		newDocument = documentRepository.save(
				DmsDocument.builder().objectName("TestTest").description("Ovo je test u testu").creator(user)
					.parentFolder(folderObject).build());
	}

	@AfterEach
	void cleanUp() {
		if (newDocument != null && documentRepository.findById(newDocument.getId()).isPresent())
			documentService.deleteById(newDocument.getId());
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
		assertEquals(0, folder.getDocuments().size());
	}

	@Test
	void deleteFolderTest() {
		DmsDocumentDTO newDocument = documentService
				.save(DmsDocument.builder().creator(user).objectName("TestTest").description("Ovo je test u testu")
						.parentFolder(folderRepository.findById(folder.getId()).orElse(null)).build());

		assertEquals(folder.getPath(), newDocument.getParentFolder().getPath());

		folderService.deleteById(folder.getId());
		
		UUID subFolderId = subFolder.getId();
		UUID newDocumentId = newDocument.getId();
		
		assertThrows(NotFoundException.class, () -> folderService.findById(subFolderId));
		assertThrows(NotFoundException.class, () -> documentService.findById(newDocumentId));
	}

	@Test
	void deleteChildrenTest() {
		DmsDocumentDTO newDocument = documentService.save(DmsDocument.builder().creator(user).objectName("TestTest")
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
	
	@Test
	void moveDocumentToDifferentFolder() {
		folder = folderService.findById(folder.getId());
		assertEquals(1, folder.getDocuments().size());
		
		subFolder = folderService.moveFilesToFolder(subFolder.getId(), Arrays.asList(newDocument.getId()));
		folder = folderService.findById(folder.getId());

		assertEquals(0, folder.getDocuments().size());
		assertEquals(1, subFolder.getDocuments().size());
	}
}
