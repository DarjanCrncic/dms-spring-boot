package com.example.dms.api.mappers;

import java.util.List;

import com.example.dms.api.dtos.BaseEntityDTO;
import com.example.dms.domain.BaseEntity;

public interface MapperInterface<T extends BaseEntity, D extends BaseEntityDTO> {
	
	D entityToDto(T object);
	
	List<D> entityListToDtoList(List<T> objectList);
}
