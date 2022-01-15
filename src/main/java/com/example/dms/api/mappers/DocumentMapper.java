package com.example.dms.api.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.dms.api.dtos.document.DocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.Document;

@Mapper(uses = UserMapper.class)
public interface DocumentMapper {

	DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);
	
	@Mapping(target = "content", ignore = true)
	Document documentDTOToDocument(DocumentDTO documentDTO);
	
	DocumentDTO documentToDocumentDTO(Document document);
	
	@Mapping(target = "creator", ignore = true)
	@Mapping(target = "content", ignore = true)
	@Mapping(target = "contentSize", ignore = true)
	@Mapping(target = "contentType", ignore = true)
	@Mapping(target = "originalFileName", ignore = true)
	Document newDocumentDTOToDocument(NewDocumentDTO documentDTO);
	
	List<DocumentDTO> documentListToDocumentDTOList(List<Document> list);
}
