package com.example.dms.services.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UpdateUserDTO;
import com.example.dms.api.mappers.UserMapper;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.UserService;
import com.example.dms.utils.exceptions.NotFoundException;
import com.example.dms.utils.exceptions.UniqueConstraintViolatedException;

@Service
public class UserServiceImpl extends EntityCrudServiceImpl<DmsUser> implements UserService{

	UserRepository userRepository;
	UserMapper userMapper;
	
	public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
		super();
		this.userRepository = userRepository;
		this.userMapper = userMapper;
	}

	@Override
	public DmsUser findByUsername(String username) {
		Optional<DmsUser> foundUser = userRepository.findByUsername(username);
		if(foundUser.isPresent()) {
			return foundUser.get();
		}
		throw new NotFoundException("User with username: '" + username + "' is not found.");
	}

	@Override
	public DmsUser findByEmail(String email) {
		Optional<DmsUser> foundUser = userRepository.findByEmail(email);
		if(foundUser.isPresent()) {
			return foundUser.get();
		}
		throw new NotFoundException("User with email: '" + email + "' is not found.");
	}

	@Override
	public DmsUser saveNewUser(NewUserDTO userDTO) {
		DmsUser user = userMapper.newUserDTOToUser(userDTO);
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new UniqueConstraintViolatedException("Following field is not unique: email, value: " + user.getEmail());
		}
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			throw new UniqueConstraintViolatedException("Following field is not unique: username, value: " + user.getUsername());
		}
		//TODO: Add automatic "home" folder creation for users
		return userRepository.save(user);
	}

	@Override
	public DmsUser updateUser(UpdateUserDTO userDTO, UUID id) {
		DmsUser user = this.findById(id);
		user.setEmail(userDTO.getEmail());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setUsername(userDTO.getUsername());
		return userRepository.save(user);
	}
}
