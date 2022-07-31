package com.example.dms.services;

import java.util.List;

import com.example.dms.domain.BaseEntity;

public interface CrudService<T extends BaseEntity, D, ID> {
	List<D> findAll();
	
	D findById(ID id);
	
	D save(T object);
	
	void deleteById(ID id);

}