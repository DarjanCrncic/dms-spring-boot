package com.example.dms.services.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UpdateUserDTO;
import com.example.dms.api.mappers.UserMapper;
import com.example.dms.domain.User;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.UserService;
import com.example.dms.utils.exceptions.NotFoundException;
import com.example.dms.utils.exceptions.UniqueConstraintViolatedException;

@Service
public class UserServiceImpl extends EntityCrudServiceImpl<User> implements UserService{

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserMapper userMapper;
	
	@Override
	public User findByUsername(String username) {
		Optional<User> foundUser = userRepository.findByUsername(username);
		if(foundUser.isPresent()) {
			return foundUser.get();
		}
		throw new NotFoundException("User with username: '" + username + "' is not found.");
	}

	@Override
	public User findByEmail(String email) {
		Optional<User> foundUser = userRepository.findByEmail(email);
		if(foundUser.isPresent()) {
			return foundUser.get();
		}
		throw new NotFoundException("User with email: '" + email + "' is not found.");
	}

	@Override
	public User saveNewUser(NewUserDTO userDTO) {
		User user = userMapper.newUserDTOToUser(userDTO);
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new UniqueConstraintViolatedException("Following field is not unique: email, value: " + user.getEmail());
		}
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			throw new UniqueConstraintViolatedException("Following field is not unique: username, value: " + user.getUsername());
		}
		return userRepository.save(user);
	}

	@Override
	public User updateUser(UpdateUserDTO userDTO, UUID id) {
		User user = this.findById(id);
		user.setEmail(userDTO.getEmail());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setUsername(userDTO.getUsername());
		return userRepository.save(user);
	}
}
