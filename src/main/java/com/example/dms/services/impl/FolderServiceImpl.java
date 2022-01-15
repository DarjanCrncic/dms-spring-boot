package com.example.dms.services.impl;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.dms.domain.DMSFolder;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.services.FolderService;
import com.example.dms.utils.Constants;
import com.example.dms.utils.exceptions.NotFoundException;

public class FolderServiceImpl extends EntityCrudServiceImpl<DMSFolder> implements FolderService {
	
	FolderRepository folderRepository;

	public FolderServiceImpl(FolderRepository folderRepository) {
		this.folderRepository = folderRepository;
	}

	@Override
	public DMSFolder findByPath(String path) {
		Optional<DMSFolder> folder = folderRepository.findByPath(path);
		if (folder.isEmpty()) {
			throw new NotFoundException("Folder with specified path and name was not found.");
		}
		return folder.get();
	}
	
	@Override
	public DMSFolder createNewFolder(String path) {
		return folderRepository.save(new DMSFolder(path));
	}
	
	public static boolean validateFolderPath(String path) {
		Pattern p = Pattern.compile(Constants.FOLDER_PATH_REGEX);
		Matcher m = p.matcher(path);  
		return m.matches();  
	}

}
