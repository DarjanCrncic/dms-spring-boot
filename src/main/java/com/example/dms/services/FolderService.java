package com.example.dms.services;

import com.example.dms.api.dtos.folder.DmsFolderDTO;
import com.example.dms.api.dtos.folder.FolderTreeDTO;
import com.example.dms.domain.DmsFolder;

import java.util.List;
import java.util.UUID;

public interface FolderService extends CrudService<DmsFolder, DmsFolderDTO, UUID>{
	
	DmsFolderDTO updateFolder(UUID id, String path);

	DmsFolderDTO moveFilesToFolder(UUID folderId, List<UUID> documentIdList);

	void deleteFolder(UUID id);

	List<FolderTreeDTO> getFolderTreeNew();

	DmsFolderDTO createFolder(String path, String username, UUID parentFolderId);
	
}
