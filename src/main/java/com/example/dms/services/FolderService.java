package com.example.dms.services;

import java.util.List;
import java.util.UUID;

import com.example.dms.api.dtos.folder.DmsFolderDTO;
import com.example.dms.domain.DmsFolder;

public interface FolderService extends CrudService<DmsFolder, DmsFolderDTO, UUID>{
	
	DmsFolderDTO findByPath(String path);

	DmsFolderDTO createFolder(String path);

	DmsFolderDTO updateFolder(UUID id, String path);

	DmsFolderDTO moveFilesToFolder(UUID folderId, List<UUID> documentIdList);
	
}
