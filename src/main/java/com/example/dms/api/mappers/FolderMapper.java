package com.example.dms.api.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.dms.api.dtos.folder.DmsFolderDTO;
import com.example.dms.api.dtos.folder.DmsFolderPathDTO;
import com.example.dms.domain.DmsFolder;

@Mapper(uses = DocumentMapper.class)
public interface FolderMapper extends MapperInterface<DmsFolder, DmsFolderDTO>  {

	FolderMapper INSTANCE = Mappers.getMapper(FolderMapper.class);
	
	@Override
	DmsFolderDTO entityToDto(DmsFolder folder);
	
	@Override
	List<DmsFolderDTO> entityListToDtoList(List<DmsFolder> folders);

	List<DmsFolderPathDTO> dmsFolderListToPathDTOList(List<DmsFolder> folders);
	
	DmsFolderPathDTO dmsFolderToPathDTO(DmsFolder folder);
}
