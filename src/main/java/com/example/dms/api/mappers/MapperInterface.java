package com.example.dms.api.mappers;

import java.util.List;

import com.example.dms.domain.BaseEntity;

public interface MapperInterface<T extends BaseEntity, D> {
	
	D entityToDto(T object);
	
	List<D> entityListToDtoList(List<T> objectList);
}
