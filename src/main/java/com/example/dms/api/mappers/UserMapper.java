package com.example.dms.api.mappers;

import com.example.dms.api.dtos.user.DmsUserDTO;
import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UpdateUserDTO;
import com.example.dms.domain.DmsUser;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserMapper extends MapperInterface<DmsUser, DmsUserDTO>{

	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
	
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "documents", ignore = true)
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "password", ignore = true)
	DmsUser userDTOToUser(DmsUserDTO userDTO);
	
	@Override
	DmsUserDTO entityToDto(DmsUser user);
	
	@Override
	List<DmsUserDTO> entityListToDtoList(List<DmsUser> list);
	
	@Mapping(target = "enabled", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "documents", ignore = true)
	@Mapping(target = "groups", ignore = true)
	DmsUser newUserDTOToUser(NewUserDTO newUserDTO);
	
	@Mapping(target = "enabled", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "creationDate", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "modifyDate", ignore = true)
	@Mapping(target = "documents", ignore = true)
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "password", ignore = true)
	void updateUserPut(UpdateUserDTO userDTO, @MappingTarget DmsUser user);
	
	@Mapping(target = "enabled", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "creationDate", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "modifyDate", ignore = true)
	@Mapping(target = "documents", ignore = true)
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "password", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateUserPatch(UpdateUserDTO userDTO, @MappingTarget DmsUser user);
	
}
