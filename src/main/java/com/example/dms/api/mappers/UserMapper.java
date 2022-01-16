package com.example.dms.api.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UserDTO;
import com.example.dms.domain.DmsUser;

@Mapper
public interface UserMapper {

	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
	
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "password", ignore = true)
	DmsUser userDTOToUser(UserDTO userDTO);
	
	UserDTO userToUserDTO(DmsUser user);
	
	List<UserDTO> userListToUserDTOList(List<DmsUser> list);
	
	@Mapping(target = "groups", ignore = true)
	DmsUser newUserDTOToUser(NewUserDTO newUserDTO);
}
