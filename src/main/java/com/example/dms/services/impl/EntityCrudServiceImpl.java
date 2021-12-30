package com.example.dms.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dms.services.CrudService;

public class EntityCrudServiceImpl<T> implements CrudService<T, Long>{

	@Autowired
	JpaRepository<T, Long> repository;
	
	@Override
	public List<T> findAll() {
		return repository.findAll();
	}

	@Override
	public T findById(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public T save(T object) {
		return repository.save(object);
	}

	@Override
	public void delete(T object) {
		repository.delete(object);
	}

	@Override
	public void deleteById(Long id) {
		repository.deleteById(id);
	}

	
}
