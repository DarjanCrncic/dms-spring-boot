package com.example.dms.api.controllers;

import com.example.dms.api.dtos.SortDTO;
import com.example.dms.api.dtos.user.DmsUserDTO;
import com.example.dms.api.dtos.user.NewUserDTO;
import com.example.dms.api.dtos.user.UpdateUserDTO;
import com.example.dms.services.UserService;
import com.example.dms.utils.exceptions.BadRequestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
	public DmsUserDTO findUserUnique(@RequestParam(required=false) String username, @RequestParam(required = false) String email) {
		if (!StringUtils.isEmpty(username))
			return userService.findByUsername(username);
		if (!StringUtils.isEmpty(email))
			return userService.findByEmail(email);

		throw new BadRequestException("Request has no username or email parameter.");
	}

	@GetMapping("/{id}")
	public DmsUserDTO findUserById(@PathVariable Integer id) {
		return userService.findById(id);
	}

	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	public DmsUserDTO createNewUser(@Valid @RequestBody NewUserDTO userDTO) {
		return userService.createUser(userDTO);
	}

	@PutMapping("/{id}")
	public DmsUserDTO updateExistingUser(@Valid @RequestBody UpdateUserDTO userDTO, @PathVariable Integer id) {
		return userService.updateUser(userDTO, id, false);
	}

	@PatchMapping("/{id}")
	public DmsUserDTO updateExistingUserPatch(@Valid @RequestBody UpdateUserDTO userDTO, @PathVariable Integer id) {
		return userService.updateUser(userDTO, id, true);
	}
}
