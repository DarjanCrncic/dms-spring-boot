package com.example.dms.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dms.domain.User;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.UserService;

@Service
public class UserServiceImpl extends EntityCrudServiceImpl<User> implements UserService{

	@Autowired
	UserRepository userRepository;
	
	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

}
