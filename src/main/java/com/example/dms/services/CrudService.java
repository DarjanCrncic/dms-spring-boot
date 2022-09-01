package com.example.dms.services;

import com.example.dms.domain.BaseEntity;

import java.util.List;

public interface CrudService<T extends BaseEntity, D, ID> {
	List<D> findAll();
	
	D findById(ID id);
	
	D save(T object);
	
	void deleteById(ID id);

}