package com.example.dms.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dms.domain.User;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.UserService;
import com.example.dms.utils.exceptions.UserNotFoundException;

@Service
public class UserServiceImpl extends EntityCrudServiceImpl<User> implements UserService{

	@Autowired
	UserRepository userRepository;
	
	@Override
	public User findByUsername(String username) {
		Optional<User> foundUser = userRepository.findByUsername(username);
		if(foundUser.isPresent()) {
			return foundUser.get();
		}
		throw new UserNotFoundException("User with username: '" + username + "' is not found.");
	}

	@Override
	public User findByEmail(String email) {
		Optional<User> foundUser = userRepository.findByEmail(email);
		if(foundUser.isPresent()) {
			return foundUser.get();
		}
		throw new UserNotFoundException("User with email: '" + email + "' is not found.");
	}
	
}
