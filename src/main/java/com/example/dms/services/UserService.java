package com.example.dms.services;

import java.util.UUID;

import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UpdateUserDTO;
import com.example.dms.domain.User;

public interface UserService extends CrudService<User, UUID>{

	User findByUsername(String username);

	User findByEmail(String email);

	User updateUser(UpdateUserDTO userDTO, UUID id);

	User saveNewUser(NewUserDTO userDTO);

}
