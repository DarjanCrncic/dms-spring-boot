package com.example.dms.api.mappers;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.DmsDocument;

@Mapper(uses = {UserMapper.class, TypeMapper.class})
public interface DocumentMapper extends MapperInterface<DmsDocument, DmsDocumentDTO> {

	DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);
	
	@Mapping(target = "imutable", ignore = true)
	@Mapping(target = "keywords", ignore = true)
	@Mapping(target = "parentFolder", ignore = true)
	@Mapping(target = "predecessorId", ignore = true)
	@Mapping(target = "rootId", ignore = true)
	@Mapping(target = "version", ignore = true)
	@Mapping(target = "content", ignore = true)
	DmsDocument documentDTOToDocument(DmsDocumentDTO documentDTO);
	
	@Override
	DmsDocumentDTO entityToDto(DmsDocument document);
	
	@Mapping(target = "imutable", ignore = true)
	@Mapping(target = "parentFolder", ignore = true)
	@Mapping(target = "predecessorId", ignore = true)
	@Mapping(target = "rootId", ignore = true)
	@Mapping(target = "version", ignore = true)
	@Mapping(target = "creator", ignore = true)
	@Mapping(target = "content", ignore = true)
	@Mapping(target = "contentSize", ignore = true)
	@Mapping(target = "contentType", ignore = true)
	@Mapping(target = "originalFileName", ignore = true)
	DmsDocument newDocumentDTOToDocument(NewDocumentDTO documentDTO);
	
	@Override
	List<DmsDocumentDTO> entityListToDtoList(List<DmsDocument> list);
	
	@Mapping(target = "creationDate", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "modifyDate", ignore = true)
	@Mapping(target = "content", ignore = true)
	@Mapping(target = "contentSize", ignore = true)
	@Mapping(target = "contentType", ignore = true)
	@Mapping(target = "creator", ignore = true)
	@Mapping(target = "imutable", ignore = true)
	@Mapping(target = "originalFileName", ignore = true)
	@Mapping(target = "parentFolder", ignore = true)
	@Mapping(target = "predecessorId", ignore = true)
	@Mapping(target = "rootId", ignore = true)
	@Mapping(target = "version", ignore = true)
	void updateDocumentPut(ModifyDocumentDTO documentDTO, @MappingTarget DmsDocument document);
	
	@Mapping(target = "creationDate", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "modifyDate", ignore = true)
	@Mapping(target = "content", ignore = true)
	@Mapping(target = "contentSize", ignore = true)
	@Mapping(target = "contentType", ignore = true)
	@Mapping(target = "creator", ignore = true)
	@Mapping(target = "imutable", ignore = true)
	@Mapping(target = "originalFileName", ignore = true)
	@Mapping(target = "parentFolder", ignore = true)
	@Mapping(target = "predecessorId", ignore = true)
	@Mapping(target = "rootId", ignore = true)
	@Mapping(target = "version", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateDocumentPatch(ModifyDocumentDTO documentDTO, @MappingTarget DmsDocument document);
}
