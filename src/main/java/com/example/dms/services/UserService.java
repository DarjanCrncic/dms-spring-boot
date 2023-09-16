package com.example.dms.services;

import com.example.dms.api.dtos.SortDTO;
import com.example.dms.api.dtos.user.DmsUserDTO;
import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UpdateUserDTO;
import com.example.dms.domain.DmsUser;

import java.util.List;

public interface UserService extends CrudService<DmsUser, DmsUserDTO, Integer>{

	DmsUserDTO findByUsername(String username);

	DmsUserDTO findByEmail(String email);

	DmsUserDTO createUser(NewUserDTO userDTO);

	DmsUserDTO updateUser(UpdateUserDTO userDTO, Integer id, boolean patch);

	List<DmsUserDTO> searchAll(String search, SortDTO sort);
}
