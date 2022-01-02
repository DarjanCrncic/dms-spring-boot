package com.example.dms.services;

import java.util.UUID;

import com.example.dms.domain.User;

import lombok.NonNull;

public interface UserService extends CrudService<User, UUID>{

	@NonNull
	User findByUsername(String username);

}
