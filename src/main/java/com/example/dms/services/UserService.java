package com.example.dms.services;

import java.util.UUID;

import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UpdateUserDTO;
import com.example.dms.api.dtos.user.UserDTO;
import com.example.dms.domain.DmsUser;

public interface UserService extends CrudService<DmsUser, UserDTO, UUID>{

	UserDTO findByUsername(String username);

	UserDTO findByEmail(String email);

	UserDTO updateUser(UpdateUserDTO userDTO, UUID id);

	UserDTO saveNewUser(NewUserDTO userDTO);

}
