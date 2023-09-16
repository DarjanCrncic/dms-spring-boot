package com.example.dms.services.impl;

import com.example.dms.api.dtos.BaseEntityDTO;
import com.example.dms.api.mappers.MapperInterface;
import com.example.dms.domain.BaseEntity;
import com.example.dms.services.DmsAclService;
import com.example.dms.utils.exceptions.DmsNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Transactional
public abstract class EntityCrudServiceImpl<T extends BaseEntity, D extends BaseEntityDTO> {

	JpaRepository<T, Integer> repository;
	MapperInterface<T, D> mapper;
	DmsAclService aclService;
	
	protected EntityCrudServiceImpl(JpaRepository<T, Integer> repository, MapperInterface<T, D> mapper, DmsAclService aclService) {
		super();
		this.repository = repository;
		this.mapper = mapper;
		this.aclService = aclService;
	}

	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','READ') "
			+ "or hasPermission(#id,'com.example.dms.domain.DmsFolder','READ') "
			+ "or hasAuthority('READ_PRIVILEGE')")
	public D findById(Integer id) {
		return mapper.entityToDto(checkPresent(id));
	}

	@PreAuthorize("hasAuthority('CREATE_PRIVILEGE')")
	public D save(T object) {
		return mapper.entityToDto(repository.saveAndFlush(object));
	}

	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','DELETE') "
			+ "or hasPermission(#id,'com.example.dms.domain.DmsFolder','DELETE') " 
			+ "or hasAuthority('DELETE_PRIVILEGE')")
	public void deleteById(Integer id) {
		aclService.removeEntriesOnDelete(checkPresent(id));
		repository.deleteById(id);
	}
	
	protected T checkPresent(Integer id) {
		Optional<T> entity = repository.findById(id);
		if (entity.isEmpty())
			throw new DmsNotFoundException("The entity with given id: '" + id + "' does not exist.");
		return entity.get();
	}
}
