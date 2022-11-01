package com.example.dms.api.mappers;

import com.example.dms.api.dtos.BaseEntityDTO;
import com.example.dms.domain.BaseEntity;
import com.example.dms.domain.security.DmsPrivilege;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface MapperInterface<T extends BaseEntity, D extends BaseEntityDTO> {
	
	D entityToDto(T object);
	
	List<D> entityListToDtoList(List<T> objectList);

	default Set<String> map(Set<DmsPrivilege> value) {
		return value.stream().map(DmsPrivilege::getName).collect(Collectors.toSet());
	}

}
