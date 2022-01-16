package com.example.dms.services.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.repositories.FolderRepository;
import com.example.dms.services.FolderService;

@SpringBootTest
class FolderServiceIntegrationTest {
	
	@Autowired
	FolderRepository folderRepository;
	
	@Autowired
	FolderService folderService;
	
	@Test
	void checkRootFolderInitialized() {
		assertTrue(folderRepository.findByPath("/").isPresent());
	}

	@Test
	@Transactional
	void createNewFolderTest() {
		folderService.createNewFolder("/test2");
		assertEquals(0, folderService.findByPath("/test2").getSubfolders().size());
	}
}
