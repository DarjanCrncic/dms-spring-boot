package com.example.dms.api.mappers;

import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.DmsDocument;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {UserMapper.class, ContentMapper.class})
public interface DocumentMapper extends MapperInterface<DmsDocument, DmsDocumentDTO> {

	DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);
	
	@Override
	@Mapping(target = "parentFolderId", source = "document.parentFolder.id")
	@Mapping(target = "type", source = "document.type.typeName")
	DmsDocumentDTO entityToDto(DmsDocument document);
	
	@Mapping(target = "type", ignore = true)
	@Mapping(target = "immutable", ignore = true)
	@Mapping(target = "parentFolder", ignore = true)
	@Mapping(target = "predecessorId", ignore = true)
	@Mapping(target = "rootId", ignore = true)
	@Mapping(target = "version", ignore = true)
	@Mapping(target = "creator", ignore = true)
	@Mapping(target = "content", ignore = true)
	DmsDocument newDocumentDTOToDocument(NewDocumentDTO documentDTO);
	
	@Override
	List<DmsDocumentDTO> entityListToDtoList(List<DmsDocument> list);
	
	@Mapping(target = "type", ignore = true)
	@Mapping(target = "creationDate", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "modifyDate", ignore = true)
	@Mapping(target = "content", ignore = true)
	@Mapping(target = "creator", ignore = true)
	@Mapping(target = "immutable", ignore = true)
	@Mapping(target = "parentFolder", ignore = true)
	@Mapping(target = "predecessorId", ignore = true)
	@Mapping(target = "rootId", ignore = true)
	@Mapping(target = "version", ignore = true)
	void updateDocumentPut(ModifyDocumentDTO documentDTO, @MappingTarget DmsDocument document);
	
	@Mapping(target = "type", ignore = true)
	@Mapping(target = "creationDate", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "modifyDate", ignore = true)
	@Mapping(target = "content", ignore = true)
	@Mapping(target = "creator", ignore = true)
	@Mapping(target = "immutable", ignore = true)
	@Mapping(target = "parentFolder", ignore = true)
	@Mapping(target = "predecessorId", ignore = true)
	@Mapping(target = "rootId", ignore = true)
	@Mapping(target = "version", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateDocumentPatch(ModifyDocumentDTO documentDTO, @MappingTarget DmsDocument document);
}
