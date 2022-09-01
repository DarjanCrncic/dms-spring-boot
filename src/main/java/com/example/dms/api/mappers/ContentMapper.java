package com.example.dms.api.mappers;

import com.example.dms.api.dtos.content.DmsContentDTO;
import com.example.dms.domain.DmsContent;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ContentMapper extends MapperInterface<DmsContent, DmsContentDTO>{

	ContentMapper INSTANCE = Mappers.getMapper(ContentMapper.class);
	
	@Override
	DmsContentDTO entityToDto(DmsContent content);
	
	@Override
	List<DmsContentDTO> entityListToDtoList(List<DmsContent> list);
	
}
