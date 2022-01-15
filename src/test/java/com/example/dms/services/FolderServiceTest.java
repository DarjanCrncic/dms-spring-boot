package com.example.dms.services;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dms.domain.DMSFolder;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.services.impl.FolderServiceImpl;
import com.example.dms.utils.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class FolderServiceTest {

	@Mock
	FolderRepository folderRepository;
	
	FolderService folderService;
	
	Optional<DMSFolder> emtpyFolder = Optional.empty();
	
	@BeforeEach
	void setUp() {
		folderService = new FolderServiceImpl(folderRepository);
	}
	
	@Test
	void folderFindByPathTest() {
		BDDMockito.given(folderRepository.findByPath(Mockito.anyString())).willReturn(emtpyFolder);
		assertThrows(NotFoundException.class, () -> folderService.findByPath(Mockito.anyString()));
	}
	
	@Test
	void testFolderRegex() {
		assertTrue(FolderServiceImpl.validateFolderPath("/test"));
		assertTrue(FolderServiceImpl.validateFolderPath("/test/test2/test1"));
		assertFalse(FolderServiceImpl.validateFolderPath("test"));
		assertFalse(FolderServiceImpl.validateFolderPath("/test/"));
	}
}
