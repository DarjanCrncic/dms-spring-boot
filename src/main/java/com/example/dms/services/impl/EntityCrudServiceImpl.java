package com.example.dms.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.api.dtos.BaseEntityDTO;
import com.example.dms.api.mappers.MapperInterface;
import com.example.dms.domain.BaseEntity;
import com.example.dms.services.DmsAclService;
import com.example.dms.utils.exceptions.DmsNotFoundException;
@Transactional
public abstract class EntityCrudServiceImpl<T extends BaseEntity, D extends BaseEntityDTO> {

	JpaRepository<T, UUID> repository;
	MapperInterface<T, D> mapper;
	DmsAclService aclService;
	
	protected EntityCrudServiceImpl(JpaRepository<T, UUID> repository, MapperInterface<T, D> mapper, DmsAclService aclService) {
		super();
		this.repository = repository;
		this.mapper = mapper;
		this.aclService = aclService;
	}

	@PostFilter("hasAuthority('READ_PRIVILEGE')")
	public List<D> findAll() {
		return mapper.entityListToDtoList(repository.findAll());
	}

	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','READ') "
			+ "or hasPermission(#id,'com.example.dms.domain.DmsFolder','READ')")
	public D findById(UUID id) {
		return mapper.entityToDto(checkPresent(id));
	}

	@PreAuthorize("hasAuthority('CREATE_PRIVILEGE')")
	public D save(T object) {
		return mapper.entityToDto(repository.saveAndFlush(object));
	}

	@PreAuthorize("hasPermission(#object.id,'com.example.dms.domain.DmsDocument','DELETE') "
			+ "or hasPermission(#object.id,'com.example.dms.domain.DmsFolder','DELETE')")
	public void delete(T object) {
		aclService.removeEntriesOnDelete(checkPresent(object.getId()));
		repository.delete(object);
	}

	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','DELETE') "
			+ "or hasPermission(#id,'com.example.dms.domain.DmsFolder','DELETE')")
	public void deleteById(UUID id) {
		aclService.removeEntriesOnDelete(checkPresent(id));
		repository.deleteById(id);
	}
	
	protected T checkPresent(UUID id) {
		Optional<T> entity = repository.findById(id);
		if (!entity.isPresent())
			throw new DmsNotFoundException("The entity with given id: '" + id + "' does not exist.");
		return entity.get();
	}
}
