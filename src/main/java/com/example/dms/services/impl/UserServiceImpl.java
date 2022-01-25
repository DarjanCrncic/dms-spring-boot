package com.example.dms.services.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UpdateUserDTO;
import com.example.dms.api.dtos.user.UserDTO;
import com.example.dms.api.mappers.UserMapper;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.UserService;
import com.example.dms.utils.exceptions.NotFoundException;
import com.example.dms.utils.exceptions.UniqueConstraintViolatedException;

@Service
@Transactional
public class UserServiceImpl extends EntityCrudServiceImpl<DmsUser, UserDTO> implements UserService{

	UserRepository userRepository;
	UserMapper userMapper;
	
	public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
		super(userRepository, userMapper);
		this.userRepository = userRepository;
		this.userMapper = userMapper;
	}

	@Override
	public UserDTO findByUsername(String username) {
		Optional<DmsUser> foundUser = userRepository.findByUsername(username);
		if(foundUser.isPresent()) {
			return userMapper.entityToDto(foundUser.get());
		}
		throw new NotFoundException("User with username: '" + username + "' is not found.");
	}

	@Override
	public UserDTO findByEmail(String email) {
		Optional<DmsUser> foundUser = userRepository.findByEmail(email);
		if(foundUser.isPresent()) {
			return userMapper.entityToDto(foundUser.get());
		}
		throw new NotFoundException("User with email: '" + email + "' is not found.");
	}

	@Override
	public UserDTO saveNewUser(NewUserDTO userDTO) {
		DmsUser user = userMapper.newUserDTOToUser(userDTO);
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new UniqueConstraintViolatedException("Following field is not unique: email, value: " + user.getEmail());
		}
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			throw new UniqueConstraintViolatedException("Following field is not unique: username, value: " + user.getUsername());
		}
		//TODO: Add automatic "home" folder creation for users
		return userMapper.entityToDto(userRepository.save(user));
	}

	@Override
	public UserDTO updateUser(UpdateUserDTO userDTO, UUID id) {
		DmsUser user = userRepository.findById(id).orElseThrow(()-> new NotFoundException());
		user.setEmail(userDTO.getEmail());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setUsername(userDTO.getUsername());
		return userMapper.entityToDto(userRepository.save(user));
	}
}
