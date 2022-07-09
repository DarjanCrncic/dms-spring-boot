package com.example.dms.services.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.api.dtos.user.DmsUserDTO;
import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UpdateUserDTO;
import com.example.dms.api.mappers.UserMapper;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.DmsAclService;
import com.example.dms.services.UserService;
import com.example.dms.utils.exceptions.DmsNotFoundException;
import com.example.dms.utils.exceptions.UniqueConstraintViolatedException;

@Service
@Transactional
public class UserServiceImpl extends EntityCrudServiceImpl<DmsUser, DmsUserDTO> implements UserService{

	UserRepository userRepository;
	UserMapper userMapper;
	
	public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, DmsAclService aclService) {
		super(userRepository, userMapper, aclService);
		this.userRepository = userRepository;
		this.userMapper = userMapper;
	}

	@Override
	@PreAuthorize("hasAuthority('READ_PRIVILEGE')")
	public DmsUserDTO findByUsername(String username) {
		Optional<DmsUser> foundUser = userRepository.findByUsername(username);
		if(foundUser.isPresent()) {
			return userMapper.entityToDto(foundUser.get());
		}
		throw new DmsNotFoundException("User with username: '" + username + "' is not found.");
	}

	@Override
	@PreAuthorize("hasAuthority('READ_PRIVILEGE')")
	public DmsUserDTO findByEmail(String email) {
		Optional<DmsUser> foundUser = userRepository.findByEmail(email);
		if(foundUser.isPresent()) {
			return userMapper.entityToDto(foundUser.get());
		}
		throw new DmsNotFoundException("User with email: '" + email + "' is not found.");
	}

	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public DmsUserDTO createUser(NewUserDTO userDTO) {
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
	@PreAuthorize("hasRole('ADMIN')")
	public DmsUserDTO updateUser(UpdateUserDTO userDTO, UUID id, boolean patch) {
		DmsUser user = userRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		if (patch) {
			userMapper.updateUserPatch(userDTO, user);
		} else {
			userMapper.updateUserPut(userDTO, user);
		}
		return userMapper.entityToDto(userRepository.save(user));
	}
}
