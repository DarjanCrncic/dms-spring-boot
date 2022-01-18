package com.example.dms.services.impl;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.domain.DmsFolder;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.services.FolderService;
import com.example.dms.utils.Constants;
import com.example.dms.utils.exceptions.BadRequestException;
import com.example.dms.utils.exceptions.NotFoundException;
import com.example.dms.utils.exceptions.UniqueConstraintViolatedException;

@Service
public class FolderServiceImpl extends EntityCrudServiceImpl<DmsFolder> implements FolderService {

	FolderRepository folderRepository;

	public FolderServiceImpl(FolderRepository folderRepository) {
		this.folderRepository = folderRepository;
	}

	@Override
	public DmsFolder findByPath(String path) {
		Optional<DmsFolder> folder = folderRepository.findByPath(path);
		if (folder.isEmpty()) {
			throw new NotFoundException("Folder with specified path: '" + path + "' was not found.");
		}
		return folder.get();
	}

	@Override
	@Transactional
	public DmsFolder createNewFolder(String path) {
		checkPath(path);
		DmsFolder parentFolder = findByPath(getParentFolderPath(path));
		
		DmsFolder newFolder = DmsFolder.builder().path(path).build();
		newFolder.setParentFolder(parentFolder);
		newFolder = folderRepository.save(newFolder);
		
		return newFolder;
	}
	
	@Override
	public DmsFolder updateFolder(UUID id, String path) {
		checkPath(path);
		DmsFolder oldFolder = findById(id);
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
		return i==0 ? "/" : path.substring(0, i);
	}


}
