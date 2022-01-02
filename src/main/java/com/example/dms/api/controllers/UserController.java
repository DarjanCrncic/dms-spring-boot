package com.example.dms.api.controllers;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	public UserDTO findUserUnique(@RequestParam Optional<String> username, @RequestParam Optional<UUID> id, @RequestParam Optional<String> email) {
		User user = null;
		if (id.isPresent()) user = userService.findById(id.get());
		else if (username.isPresent()) user = userService.findByUsername(username.get());
		else if (email.isPresent()) user = userService.findByEmail(email.get());
		else throw new BadRequestException("Request has no username, id or email parameter.");
		return userMapper.userToUserDTO(user);
	}
	
}
