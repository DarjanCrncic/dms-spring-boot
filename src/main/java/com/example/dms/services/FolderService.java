package com.example.dms.services;

import java.util.UUID;

import com.example.dms.domain.DmsFolder;

public interface FolderService extends CrudService<DmsFolder, UUID>{
	
	DmsFolder findByPath(String path);

	DmsFolder createNewFolder(String path);
	
}
