package com.example.dms.api.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.dms.api.dtos.document.DocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.DmsDocument;

@Mapper(uses = UserMapper.class)
public interface DocumentMapper {

	DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);
	
	@Mapping(target = "content", ignore = true)
	DmsDocument documentDTOToDocument(DocumentDTO documentDTO);
	
	DocumentDTO documentToDocumentDTO(DmsDocument document);
	
	@Mapping(target = "creator", ignore = true)
	@Mapping(target = "content", ignore = true)
	@Mapping(target = "contentSize", ignore = true)
	@Mapping(target = "contentType", ignore = true)
	@Mapping(target = "originalFileName", ignore = true)
	DmsDocument newDocumentDTOToDocument(NewDocumentDTO documentDTO);
	
	List<DocumentDTO> documentListToDocumentDTOList(List<DmsDocument> list);
}
