package com.example.dms.services.impl;

import com.example.dms.api.dtos.SortDTO;
import com.example.dms.api.dtos.user.DmsUserDTO;
import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UpdateUserDTO;
import com.example.dms.api.mappers.UserMapper;
import com.example.dms.domain.DmsUser;
import com.example.dms.domain.security.DmsPrivilege;
import com.example.dms.domain.security.DmsRole;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.DmsAclService;
import com.example.dms.services.RolePrivilegeService;
import com.example.dms.services.UserService;
import com.example.dms.services.search.SpecificationBuilder;
import com.example.dms.services.search.user.UserSpecProvider;
import com.example.dms.utils.Utils;
import com.example.dms.utils.exceptions.DmsNotFoundException;
import com.example.dms.utils.exceptions.UniqueConstraintViolatedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl extends EntityCrudServiceImpl<DmsUser, DmsUserDTO> implements UserService{

	UserRepository userRepository;
	UserMapper userMapper;
	RolePrivilegeService rolePrivilegeService;

	BCryptPasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, DmsAclService aclService,
						   RolePrivilegeService rolePrivilegeService, BCryptPasswordEncoder passwordEncoder) {
		super(userRepository, userMapper, aclService);
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.rolePrivilegeService = rolePrivilegeService;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	@PreAuthorize("hasRole('USER')")
	public List<DmsUserDTO> findAll() {
		return userMapper.entityListToDtoList(userRepository.findAll());
	}
	
	@Override
	@PreAuthorize("hasRole('USER')")
	public DmsUserDTO findByUsername(String username) {
		Optional<DmsUser> foundUser = userRepository.findByUsername(username);
		if(foundUser.isPresent()) {
			return userMapper.entityToDto(foundUser.get());
		}
		throw new DmsNotFoundException("User with username: '" + username + "' is not found.");
	}

	@Override
	@PreAuthorize("hasRole('USER')")
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
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		mapRolesPrivilegesToUser(user, userDTO.getRole(), userDTO.getPrivileges());
		return userMapper.entityToDto(userRepository.save(user));
	}

	private void mapRolesPrivilegesToUser(DmsUser user, String roleName, List<String> privilegesNames) {
		if (roleName != null) {
			List<DmsRole> roles = new ArrayList<>();
			roles.add(rolePrivilegeService.findRoleByName(roleName));
			user.setRoles(roles);
		}
		if (privilegesNames != null) {
			List<DmsPrivilege> privileges = rolePrivilegeService.findPrivilegesByNames(privilegesNames);
			user.setPrivileges(privileges);
		}
	}

	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public DmsUserDTO updateUser(UpdateUserDTO userDTO, UUID id, boolean patch) {
		DmsUser user = userRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		String oldUsername = user.getUsername();
		if (patch) {
			userMapper.updateUserPatch(userDTO, user);
		} else {
			userMapper.updateUserPut(userDTO, user);
		}
		if (userDTO.getUsername() != null && !oldUsername.equals(userDTO.getUsername())) {
			userRepository.updateUsername(oldUsername, userDTO.getUsername());
		}
		mapRolesPrivilegesToUser(user, userDTO.getRole(), userDTO.getPrivileges());
		return userMapper.entityToDto(userRepository.save(user));
	}

	@Override
	public List<DmsUserDTO> searchAll(String search, SortDTO sort) {
		if (search != null) {
			SpecificationBuilder<DmsUser> builder = new SpecificationBuilder<>(new UserSpecProvider());
			return userMapper
					.entityListToDtoList(userRepository.findAll(builder.parse(search), Utils.toSort(sort)));
		}
		return userMapper.entityListToDtoList(userRepository.findAll(Utils.toSort(sort)));
	}
}
