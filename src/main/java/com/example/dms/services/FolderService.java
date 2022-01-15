package com.example.dms.services;

import java.util.UUID;

import com.example.dms.domain.DMSFolder;

public interface FolderService extends CrudService<DMSFolder, UUID>{
	
	DMSFolder findByPath(String path);

	DMSFolder createNewFolder(String path);
	
}
