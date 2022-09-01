package com.example.dms.api.controllers;

import com.example.dms.api.dtos.group.DmsGroupDTO;
import com.example.dms.api.dtos.group.NewGroupDTO;
import com.example.dms.services.GroupService;
import com.example.dms.utils.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {

	private final GroupService groupService;

	@GetMapping
	public List<DmsGroupDTO> getAllGroups() {
		return groupService.findAll();
	}

	@GetMapping("/search")
	public DmsGroupDTO getGroupByReqParam(@RequestParam Optional<String> name) {
		if (name.isPresent()) {
			return groupService.findGroupByGroupName(name.get());
		}
		throw new BadRequestException("Request parameters for search are invalid.");
	}

	@GetMapping("/{id}")
	public DmsGroupDTO getGroupById(@PathVariable UUID id) {
		return groupService.findById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public DmsGroupDTO createNewGroup(@RequestBody @Valid NewGroupDTO groupDTO) {
		return groupService.createGroup(groupDTO);
	}


	@PutMapping("/{id}")
	public DmsGroupDTO updateGroup(@PathVariable UUID id, @RequestBody @Valid NewGroupDTO groupDTO) {
		return groupService.updateGroup(id, groupDTO);
	}

	@DeleteMapping("/{id}")
	public void deleteGroupById(@PathVariable UUID id) {
		groupService.deleteById(id);
	}

	@PostMapping("/users/{id}")
	public DmsGroupDTO addUsersToGroup(@PathVariable UUID id, @RequestBody List<UUID> idList) {
		return groupService.addUsersToGroup(id, idList);
	}
}
