package com.example.dms.api.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.dms.api.dtos.folder.DmsFolderDTO;
import com.example.dms.api.dtos.folder.FolderTreeDTO;
import com.example.dms.domain.DmsFolder;

@Mapper(uses = DocumentMapper.class)
public interface FolderMapper extends MapperInterface<DmsFolder, DmsFolderDTO>  {

	FolderMapper INSTANCE = Mappers.getMapper(FolderMapper.class);
	
	@Override
	@Mapping(source = "parentFolder.path", target = "parentFolder")
	DmsFolderDTO entityToDto(DmsFolder folder);
	
	List<String> foldersToPaths(List<DmsFolder> folders);
	
	default String folderToPath(DmsFolder folder) {
		return folder.getPath();
	}
	
	@Override
	List<DmsFolderDTO> entityListToDtoList(List<DmsFolder> folders);

	@Mapping(target = "numOfDocuments", ignore = true)
	@Mapping(source = "parentFolder.path", target = "parentFolder")
	FolderTreeDTO dmsFolderToFolderTree(DmsFolder folder);
	
	List<FolderTreeDTO> dmsFolderListToFolderTreeList(List<DmsFolder> folders);
}
