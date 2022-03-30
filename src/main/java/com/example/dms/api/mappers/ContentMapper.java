package com.example.dms.api.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.dms.api.dtos.content.DmsContentDTO;
import com.example.dms.domain.DmsContent;

@Mapper
public interface ContentMapper extends MapperInterface<DmsContent, DmsContentDTO>{

	ContentMapper INSTANCE = Mappers.getMapper(ContentMapper.class);
	
	@Override
	DmsContentDTO entityToDto(DmsContent content);
	
	@Override
	List<DmsContentDTO> entityListToDtoList(List<DmsContent> list);
	
}
