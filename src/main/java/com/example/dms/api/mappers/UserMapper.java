package com.example.dms.api.mappers;

import com.example.dms.api.dtos.user.DmsUserDTO;
import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UpdateUserDTO;
import com.example.dms.domain.DmsUser;
import com.example.dms.domain.security.DmsRole;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

@Mapper
public interface UserMapper extends MapperInterface<DmsUser, DmsUserDTO>{

	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

	@Override
	@Mapping(target = "role", expression = "java(getRole(user))")
	@Mapping(target = "privileges", expression = "java(map(user.getPrivileges()))")
	DmsUserDTO entityToDto(DmsUser user);

	default String getRole(DmsUser user) {
		DmsRole role = user.getRoles().stream().findAny().orElse(null);
		return role != null ? role.getName() : null;
	}

	@Override
	List<DmsUserDTO> entityListToDtoList(List<DmsUser> list);
	Set<DmsUserDTO> entityListToDtoList(Set<DmsUser> list);

	@Mapping(target = "enabled", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "privileges", ignore = true)
	@Mapping(target = "documents", ignore = true)
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "documentColumnPreferences", ignore = true)
	DmsUser newUserDTOToUser(NewUserDTO newUserDTO);

	@Mapping(target = "enabled", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "privileges", ignore = true)
	@Mapping(target = "creationDate", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "modifyDate", ignore = true)
	@Mapping(target = "documents", ignore = true)
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "documentColumnPreferences", ignore = true)
	void updateUserPut(UpdateUserDTO userDTO, @MappingTarget DmsUser user);

	@Mapping(target = "enabled", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "privileges", ignore = true)
	@Mapping(target = "creationDate", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "modifyDate", ignore = true)
	@Mapping(target = "documents", ignore = true)
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "documentColumnPreferences", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateUserPatch(UpdateUserDTO userDTO, @MappingTarget DmsUser user);



}
