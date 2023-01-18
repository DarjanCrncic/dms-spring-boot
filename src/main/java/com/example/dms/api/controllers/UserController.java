package com.example.dms.api.controllers;

import com.example.dms.api.dtos.SortDTO;
import com.example.dms.api.dtos.user.DmsUserDTO;
import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UpdateUserDTO;
import com.example.dms.services.UserService;
import com.example.dms.utils.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	@Autowired
	UserService userService;
	
	@GetMapping
	public List<DmsUserDTO> getAllUsers(@RequestParam(required = false) String search, SortDTO sort) {
		return userService.searchAll(search, sort);
	}
	
	@GetMapping("/details")
	public DmsUserDTO getUserData(@RequestParam String username) {
		return userService.findByUsername(username);
	}

	@GetMapping("/search")
	public DmsUserDTO findUserUnique(@RequestParam Optional<String> username, @RequestParam Optional<String> email) {
		DmsUserDTO user = null;
		if (username.isPresent())
			user = userService.findByUsername(username.get());
		else if (email.isPresent())
			user = userService.findByEmail(email.get());
		else
			throw new BadRequestException("Request has no username or email parameter.");
		return user;
	}

	@GetMapping("/{id}")
	public DmsUserDTO findUserById(@PathVariable UUID id) {
		return userService.findById(id);
	}

	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	public DmsUserDTO createNewUser(@Valid @RequestBody NewUserDTO userDTO) {
		return userService.createUser(userDTO);
	}

	@PutMapping("/{id}")
	public DmsUserDTO updateExistingUser(@Valid @RequestBody UpdateUserDTO userDTO, @PathVariable UUID id) {
		return userService.updateUser(userDTO, id, false);
	}
	
	@PatchMapping("/{id}")
	public DmsUserDTO updateExistingUserPatch(@Valid @RequestBody UpdateUserDTO userDTO, @PathVariable UUID id) {
		return userService.updateUser(userDTO, id, true);
	}
}
