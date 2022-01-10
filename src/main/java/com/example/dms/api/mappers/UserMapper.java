package com.example.dms.api.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UserDTO;
import com.example.dms.domain.User;

@Mapper
public interface UserMapper {

	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
	
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "password", ignore = true)
	User userDTOToUser(UserDTO userDTO);
	
	UserDTO userToUserDTO(User user);
	
	List<UserDTO> userListToUserDTOList(List<User> list);
	
	@Mapping(target = "groups", ignore = true)
	User newUserDTOToUser(NewUserDTO newUserDTO);
}
