package com.example.dms.api.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.dms.api.dtos.user.UserDTO;
import com.example.dms.domain.User;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
	
	@Mapping(target = "password", ignore = true)
	User userDTOToUser(UserDTO userDTO);
	
	UserDTO userToUserDTO(User user);
	
	List<UserDTO> userListToUserDTOList(List<User> list);
}
