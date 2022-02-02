package com.example.dms.api.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.dms.api.dtos.type.DmsTypeDTO;
import com.example.dms.domain.DmsType;

@Mapper
public interface TypeMapper extends MapperInterface<DmsType, DmsTypeDTO>{

	TypeMapper INSTANCE = Mappers.getMapper(TypeMapper.class);
	
	@Override
	DmsTypeDTO entityToDto(DmsType dmsType);
	
	@Override
	List<DmsTypeDTO> entityListToDtoList(List<DmsType> list);
	
}