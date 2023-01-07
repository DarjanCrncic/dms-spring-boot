package com.example.dms.services.impl;

import com.example.dms.api.dtos.SortDTO;
import com.example.dms.api.dtos.user.DmsUserDTO;
import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UpdateUserDTO;
import com.example.dms.api.mappers.UserMapper;
import com.example.dms.domain.DmsUser;
import com.example.dms.domain.security.DmsPrivilege;
import com.example.dms.domain.security.DmsRole;
import com.example.dms.repositories.GroupRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl extends EntityCrudServiceImpl<DmsUser, DmsUserDTO> implements UserService{

	UserRepository userRepository;
	UserMapper userMapper;
	RolePrivilegeService rolePrivilegeService;

	BCryptPasswordEncoder passwordEncoder;
	GroupRepository groupRepository;

	public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, DmsAclService aclService,
						   RolePrivilegeService rolePrivilegeService, BCryptPasswordEncoder passwordEncoder,
						   GroupRepository groupRepository) {
		super(userRepository, userMapper, aclService);
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.rolePrivilegeService = rolePrivilegeService;
		this.passwordEncoder = passwordEncoder;
		this.groupRepository = groupRepository;
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
		checkUser(userDTO.getUsername(), userDTO.getEmail(), null);
		DmsUser user = userMapper.newUserDTOToUser(userDTO);

		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		mapRolesPrivilegesToUser(user, userDTO.getRole(), userDTO.getPrivileges());
		return userMapper.entityToDto(userRepository.save(user));
	}

	private void mapRolesPrivilegesToUser(DmsUser user, String roleName, List<String> privilegesNames) {
		if (roleName != null) {
			Set<DmsRole> roles = new HashSet<>();
			roles.add(rolePrivilegeService.findRoleByName(roleName));
			user.setRoles(roles);
		}
		if (privilegesNames != null) {
			Set<DmsPrivilege> privileges = new HashSet<>(rolePrivilegeService.findPrivilegesByNames(privilegesNames));
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
		checkUser(user.getUsername(), user.getEmail(), user.getId());
		if (userDTO.getUsername() != null && !oldUsername.equals(userDTO.getUsername())) {
			userRepository.updateUsername(oldUsername, userDTO.getUsername());
		}
		mapRolesPrivilegesToUser(user, userDTO.getRole(), userDTO.getPrivileges());
		return userMapper.entityToDto(userRepository.save(user));
	}

	void checkUser(String username, String email, UUID id) {
		Optional<DmsUser> userUsername = userRepository.findByUsername(username);
		Optional<DmsUser> userEmail = userRepository.findByEmail(email);

		if (userEmail.isPresent() && (id == null || !userEmail.get().getId().equals(id))) {
			throw new UniqueConstraintViolatedException("User email must be unique.");
		}
		if (userUsername.isPresent() && (id == null || !userUsername.get().getId().equals(id))) {
			throw new UniqueConstraintViolatedException("Username must be unique");
		}
		if (groupRepository.findByIdentifier(username).isPresent()) {
			throw new UniqueConstraintViolatedException("Username must be different from any existing group identifier.");
		}
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

	@Override
	public void deleteById(UUID id) {
		DmsUser user = checkPresent(id);
		userRepository.removeAclEntries(user.getUsername());
		super.deleteById(id);
	}
}
