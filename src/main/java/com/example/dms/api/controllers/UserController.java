package com.example.dms.api.controllers;

import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.dms.api.dtos.user.UserDTO;
import com.example.dms.api.mappers.UserMapper;
import com.example.dms.domain.User;
import com.example.dms.services.UserService;
import com.example.dms.utils.exceptions.BadRequestException;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	@Autowired
	UserMapper userMapper;
	
	@Autowired
	UserService userService;
	
	@GetMapping
	public UserDTO findUserUnique(@RequestParam Optional<String> username, @RequestParam Optional<String> email) {
		User user = null;
		if (username.isPresent()) user = userService.findByUsername(username.get());
		else if (email.isPresent()) user = userService.findByEmail(email.get());
		else throw new BadRequestException("Request has no username, id or email parameter.");
		return userMapper.userToUserDTO(user);
	}
	
	@GetMapping("/{id}")
	public UserDTO findUserById(@PathVariable UUID id) {
		return userMapper.userToUserDTO(userService.findById(id));
	}
	
	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	public UserDTO createNewUser(@Valid @RequestBody User user) {
		return userMapper.userToUserDTO(userService.save(user));
	}
}
