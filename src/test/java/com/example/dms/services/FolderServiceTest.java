package com.example.dms.services;

import com.example.dms.api.dtos.folder.DmsFolderDTO;
import com.example.dms.api.dtos.folder.NewFolderDTO;
import com.example.dms.api.mappers.FolderMapper;
import com.example.dms.domain.DmsFolder;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.services.impl.FolderServiceImpl;
import com.example.dms.utils.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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

	Optional<DmsFolder> emptyFolder = Optional.empty();
	Optional<DmsFolder> rootFolder = Optional.of(DmsFolder.builder().name("/").build());

	DmsFolder validFolder = DmsFolder.builder().name("test").parentFolder(rootFolder.get()).build();
	NewFolderDTO folderDTO = NewFolderDTO.builder().name("test").build();
	DmsFolderDTO createdfolderDTO = DmsFolderDTO.builder().name("test").build();
	DmsFolderDTO rootFolderDTO = DmsFolderDTO.builder().name("/").build();

	@Test
	void folderFindByPathTest() {
//		BDDMockito.given(folderRepository.findByPath(Mockito.anyString())).willReturn(emptyFolder);
//		assertThrows(DmsNotFoundException.class, () -> folderService.findByPath("/test"));
	}

	@Test
	void testFolderRegex() {
		assertTrue(StringUtils.validateFolderName("test"));
		assertTrue(StringUtils.validateFolderName("test1_2"));
		assertFalse(StringUtils.validateFolderName("/test"));
		assertFalse(StringUtils.validateFolderName("test/"));
		assertFalse(StringUtils.validateFolderName("//test"));
	}

}
