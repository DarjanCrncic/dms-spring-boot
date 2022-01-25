package com.example.dms.api.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UserDTO;
import com.example.dms.domain.DmsUser;

@Mapper
public interface UserMapper extends MapperInterface<DmsUser, UserDTO>{

	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
	
	@Mapping(target = "documents", ignore = true)
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "password", ignore = true)
	DmsUser userDTOToUser(UserDTO userDTO);
	
	@Override
	UserDTO entityToDto(DmsUser user);
	
	@Override
	List<UserDTO> entityListToDtoList(List<DmsUser> list);
	
	@Mapping(target = "documents", ignore = true)
	@Mapping(target = "groups", ignore = true)
	DmsUser newUserDTOToUser(NewUserDTO newUserDTO);
}
