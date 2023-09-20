package com.example.dms.services;

import com.example.dms.api.dtos.folder.DmsFolderDTO;
import com.example.dms.api.dtos.folder.FolderTreeDTO;
import com.example.dms.api.dtos.folder.NewFolderDTO;
import com.example.dms.domain.DmsFolder;

import java.util.List;

public interface FolderService extends CrudService<DmsFolder, DmsFolderDTO, Integer>{
	
	DmsFolderDTO updateFolder(Integer id, String path);

	DmsFolderDTO moveFilesToFolder(Integer folderId, List<Integer> documentIdList);

	void deleteFolder(Integer id);

	List<FolderTreeDTO> getFolderTreeNew();

	DmsFolderDTO createFolder(NewFolderDTO newFolderDTO);

}
