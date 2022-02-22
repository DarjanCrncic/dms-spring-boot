package com.example.dms.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.api.mappers.MapperInterface;
import com.example.dms.domain.BaseEntity;
import com.example.dms.services.CrudService;
import com.example.dms.utils.exceptions.DmsNotFoundException;
@Transactional
public abstract class EntityCrudServiceImpl<T extends BaseEntity, D> implements CrudService<T, D, UUID>{

	JpaRepository<T, UUID> repository;
	
	MapperInterface<T, D> mapper;
	
	protected EntityCrudServiceImpl(JpaRepository<T, UUID> repository, MapperInterface<T, D> mapper) {
		super();
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	@PostFilter("hasAuthority('READ_PRIVILEGE')")
	public List<D> findAll() {
		return mapper.entityListToDtoList(repository.findAll());
	}

	@Override
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','READ')")
	public D findById(UUID id) {
		Optional<T> entity = repository.findById(id);
		if (!entity.isPresent())
			throw new DmsNotFoundException("The entity with requested id: '" + id + "' does not exist.");
		return mapper.entityToDto(entity.get());
	}

	@Override
	@PreAuthorize("hasAuthority('CREATE_PRIVILEGE')")
	public D save(T object) {
		return mapper.entityToDto(repository.save(object));
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id,'com.example.dms.domain.DmsDocument','DELETE')")
	public void delete(T object) {
		repository.delete(object);
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id,'com.example.dms.domain.DmsDocument','DELETE')")
	public void deleteById(UUID id) {
		repository.deleteById(id);
	}
}
