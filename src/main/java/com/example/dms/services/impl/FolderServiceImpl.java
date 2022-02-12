package com.example.dms.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.api.dtos.folder.DmsFolderDTO;
import com.example.dms.api.mappers.FolderMapper;
import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsFolder;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.services.FolderService;
import com.example.dms.utils.Constants;
import com.example.dms.utils.exceptions.BadRequestException;
import com.example.dms.utils.exceptions.DmsNotFoundException;
import com.example.dms.utils.exceptions.UniqueConstraintViolatedException;

@Service
@Transactional
public class FolderServiceImpl extends EntityCrudServiceImpl<DmsFolder, DmsFolderDTO> implements FolderService {

	FolderRepository folderRepository;
	FolderMapper folderMapper;
	DocumentRepository documentRepository;

	public FolderServiceImpl(FolderRepository folderRepository, FolderMapper folderMapper, DocumentRepository documentRepository) {
		super(folderRepository, folderMapper);
		this.folderRepository = folderRepository;
		this.folderMapper = folderMapper;
		this.documentRepository = documentRepository;
	}

	@Override
	public DmsFolderDTO findByPath(String path) {
		Optional<DmsFolder> folder = folderRepository.findByPath(path);
		if (folder.isEmpty()) {
			throw new DmsNotFoundException("Folder with specified path: '" + path + "' was not found.");
		}
		return folderMapper.entityToDto(folder.get());
	}

	@Override
	public DmsFolderDTO createNewFolder(String path) {
		checkPath(path);
		DmsFolder parentFolder = folderRepository.findByPath(getParentFolderPath(path))
				.orElseThrow(DmsNotFoundException::new);
		DmsFolder newFolder = DmsFolder.builder().path(path).build();

		persistFolderToParentFolder(parentFolder, newFolder);
		return folderMapper.entityToDto(newFolder);
	}

	@Override
	public DmsFolderDTO updateFolder(UUID id, String path) {
		checkPath(path);
		DmsFolder oldFolder = folderRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		oldFolder.setPath(path);
		return save(oldFolder);
	}

	private void checkPath(String path) {
		if (!validateFolderPath(path)) {
			throw new BadRequestException("Folder path: '" + path + "' does not match required parameters.");
		}
		if (folderRepository.findByPath(path).isPresent()) {
			throw new UniqueConstraintViolatedException("Folder with path: '" + path + "' already exists.");
		}
	}

	public static boolean validateFolderPath(String path) {
		Pattern p = Pattern.compile(Constants.FOLDER_PATH_REGEX);
		Matcher m = p.matcher(path);
		return m.matches();
	}

	public static String getParentFolderPath(String path) {
		int i = path.lastIndexOf("/");
		return i == 0 ? "/" : path.substring(0, i);
	}

	@Override
	public DmsFolderDTO moveFilesToFolder(UUID folderId, List<UUID> documentIdList) {
		DmsFolder folder = folderRepository.findById(folderId)
				.orElseThrow(() -> new DmsNotFoundException("Folder with specified id: " + folderId + " could not be found."));
		for (UUID documentId : documentIdList) {
			DmsDocument doc = documentRepository.findById(documentId)
					.orElseThrow(() -> new BadRequestException("Ivalid document id: " + documentId + "."));
			persistDocumentToFolder(folder, doc);
		}
		return folderMapper.entityToDto(folder);
	}
	
	private void persistDocumentToFolder(DmsFolder folder, DmsDocument document) {
		if (!folder.getDocuments().contains(document)) {
			document.setParentFolder(folder);
			document = documentRepository.save(document);
			folder.getDocuments().add(document);
			folder = folderRepository.save(folder);
		}
	}
	
	private void persistFolderToParentFolder(DmsFolder parentFolder, DmsFolder newFolder) {
		if (!parentFolder.getSubfolders().contains(newFolder)) {
			newFolder.setParentFolder(parentFolder);
			newFolder = folderRepository.save(newFolder);
			parentFolder.getSubfolders().add(newFolder);
			parentFolder = folderRepository.save(parentFolder);
		}
	}
	
	// TODO: copy file to folder 
	// TODO: move subfolder to other folder
	// TODO: copy folder to another folder
}
