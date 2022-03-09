package com.example.dms.services;

import java.util.UUID;

import com.example.dms.api.dtos.user.DmsUserDTO;
import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UpdateUserDTO;
import com.example.dms.domain.DmsUser;

public interface UserService extends CrudService<DmsUser, DmsUserDTO, UUID>{

	DmsUserDTO findByUsername(String username);

	DmsUserDTO findByEmail(String email);

	DmsUserDTO createUser(NewUserDTO userDTO);

	DmsUserDTO updateUser(UpdateUserDTO userDTO, UUID id, boolean patch);

}
