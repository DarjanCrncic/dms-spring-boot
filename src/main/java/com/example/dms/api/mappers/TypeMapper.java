package com.example.dms.api.mappers;

import com.example.dms.api.dtos.type.DmsTypeDTO;
import com.example.dms.domain.DmsType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TypeMapper extends MapperInterface<DmsType, DmsTypeDTO>{

	TypeMapper INSTANCE = Mappers.getMapper(TypeMapper.class);
	
	@Override
	DmsTypeDTO entityToDto(DmsType dmsType);
	
	@Override
	List<DmsTypeDTO> entityListToDtoList(List<DmsType> list);
	
}