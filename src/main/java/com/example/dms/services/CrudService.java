package com.example.dms.services;

import java.util.List;

import com.example.dms.domain.BaseEntity;

public interface CrudService<T extends BaseEntity, ID> {
	List<T> findAll();
	
	T findById(ID id);
	
	T save(T object);
	
	void delete(T object);
	
	void deleteById(ID id);

	T refresh(T object);
}