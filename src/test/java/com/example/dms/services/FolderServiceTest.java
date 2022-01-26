package com.example.dms.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dms.api.dtos.folder.NewFolderDTO;
import com.example.dms.api.mappers.FolderMapper;
import com.example.dms.domain.DmsFolder;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.services.impl.FolderServiceImpl;
import com.example.dms.utils.exceptions.BadRequestException;
import com.example.dms.utils.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class FolderServiceTest {

	@Mock
	FolderRepository folderRepository;

	@Mock
	FolderMapper folderMapper;
	
	FolderService folderService;

	Optional<DmsFolder> emptyFolder = Optional.empty();
	Optional<DmsFolder> rootFolder = Optional.of(DmsFolder.builder().path("/").build());

	DmsFolder validFolder = DmsFolder.builder().path("/test").parentFolder(rootFolder.get()).build();
	NewFolderDTO folderDTO = NewFolderDTO.builder().path("/test").build();

	@BeforeEach
	void setUp() {
		folderService = new FolderServiceImpl(folderRepository,folderMapper);
	}

	@Test
	void folderFindByPathTest() {
		BDDMockito.given(folderRepository.findByPath(Mockito.anyString())).willReturn(emptyFolder);
		String path = Mockito.anyString();
		assertThrows(NotFoundException.class, () -> folderService.findByPath(path));
	}

	@Test
	void testFolderRegex() {
		assertTrue(FolderServiceImpl.validateFolderPath("/test"));
		assertTrue(FolderServiceImpl.validateFolderPath("/test/test2/test1"));
		assertFalse(FolderServiceImpl.validateFolderPath("test"));
		assertFalse(FolderServiceImpl.validateFolderPath("/test/"));
	}

	@Test
	void testParentFolderExtraction() {
		assertEquals("/test1", FolderServiceImpl.getParentFolderPath("/test1/test2"));
		assertEquals("/test1/test2", FolderServiceImpl.getParentFolderPath("/test1/test2/test3"));

		assertEquals("/", FolderServiceImpl.getParentFolderPath("/test1"));
	}

	@Test
	void testIvalidFolderPathWhenCreating() {
		assertThrows(BadRequestException.class, () -> folderService.createNewFolder("notStartingWithFrontSlash"));
		assertThrows(BadRequestException.class, () -> folderService.createNewFolder("/endingWithFrontSlash/"));
	}

	@Test
	void testCreateFolder() {
		BDDMockito.when(folderRepository.findByPath(Mockito.anyString())).thenReturn(emptyFolder)
				.thenReturn(rootFolder);
		folderService.createNewFolder("/test");

		verify(folderRepository, times(2)).findByPath(Mockito.anyString());
	}
}
