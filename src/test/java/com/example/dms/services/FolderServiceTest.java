package com.example.dms.services;

import com.example.dms.api.mappers.FolderMapper;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.services.impl.FolderServiceImpl;
import com.example.dms.utils.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class FolderServiceTest {

	@Mock
	FolderRepository folderRepository;

	@Mock
	FolderMapper folderMapper;
	
	@Mock
	DocumentRepository documentRepository;
	
	@Mock
	DmsAclService aclService;
	
	@Mock
	DocumentService documentService;
	
	@InjectMocks
	FolderServiceImpl folderService;

	@Test
	void testFolderRegex() {
		assertTrue(StringUtils.validateFolderName("test"));
		assertTrue(StringUtils.validateFolderName("test1_2"));
		assertFalse(StringUtils.validateFolderName("/test"));
		assertFalse(StringUtils.validateFolderName("test/"));
		assertFalse(StringUtils.validateFolderName("//test"));
	}

}
