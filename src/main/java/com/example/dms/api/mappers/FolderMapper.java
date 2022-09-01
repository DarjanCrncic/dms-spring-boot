package com.example.dms.api.mappers;

import com.example.dms.api.dtos.folder.DmsFolderDTO;
import com.example.dms.api.dtos.folder.FolderTreeDTO;
import com.example.dms.domain.DmsFolder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

@Mapper(uses = DocumentMapper.class)
public interface FolderMapper extends MapperInterface<DmsFolder, DmsFolderDTO>  {

	FolderMapper INSTANCE = Mappers.getMapper(FolderMapper.class);
	
	@Override
	@Mapping(source = "parentFolder.id", target = "parentFolderId")
	DmsFolderDTO entityToDto(DmsFolder folder);
	
	List<UUID> foldersToIds(List<DmsFolder> folders);
	
	default UUID folderToId(DmsFolder folder) {
		return folder.getId();
	}
	
	@Override
	List<DmsFolderDTO> entityListToDtoList(List<DmsFolder> folders);

	@Mapping(target = "numOfDocuments", ignore = true)
	@Mapping(source = "parentFolder.id", target = "parentFolderId")
	FolderTreeDTO dmsFolderToFolderTree(DmsFolder folder);
	
	List<FolderTreeDTO> dmsFolderListToFolderTreeList(List<DmsFolder> folders);
}
