package com.example.dms.services;

import java.util.UUID;

import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UpdateUserDTO;
import com.example.dms.domain.DmsUser;

public interface UserService extends CrudService<DmsUser, UUID>{

	DmsUser findByUsername(String username);

	DmsUser findByEmail(String email);

	DmsUser updateUser(UpdateUserDTO userDTO, UUID id);

	DmsUser saveNewUser(NewUserDTO userDTO);

}
