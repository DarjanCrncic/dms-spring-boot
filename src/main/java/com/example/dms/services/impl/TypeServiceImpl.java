package com.example.dms.services.impl;

import com.example.dms.api.dtos.type.DmsTypeDTO;
import com.example.dms.api.dtos.type.NewTypeDTO;
import com.example.dms.api.mappers.TypeMapper;
import com.example.dms.domain.DmsType;
import com.example.dms.repositories.TypeRepository;
import com.example.dms.services.DmsAclService;
import com.example.dms.services.TypeService;
import com.example.dms.utils.exceptions.UniqueConstraintViolatedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@PreAuthorize("hasRole('ADMIN')")
public class TypeServiceImpl extends EntityCrudServiceImpl<DmsType, DmsTypeDTO> implements TypeService {

	private final TypeRepository typeRepository;
	private final TypeMapper typeMapper;

	protected TypeServiceImpl(TypeRepository repository, TypeMapper mapper, DmsAclService aclService) {
		super(repository, mapper, aclService);
		this.typeMapper = mapper;
		this.typeRepository = repository;
	}
	
	@Override
	@PreAuthorize("hasRole('USER')")
	public List<DmsTypeDTO> findAll() {
		return this.typeMapper.entityListToDtoList(typeRepository.findAll());
	}

	@Override
	@PreAuthorize("hasAuthority('CREATE_PRIVILEGE')")
	public DmsTypeDTO createType(NewTypeDTO dmsTypeDTO) {
		if (typeRepository.existsByTypeName(dmsTypeDTO.getTypeName())) {
			throw new UniqueConstraintViolatedException(
					"Type with type name: " + dmsTypeDTO.getTypeName() + "already exists.");
		}
		return this.save(DmsType.builder().typeName(dmsTypeDTO.getTypeName()).build());
	}
}
