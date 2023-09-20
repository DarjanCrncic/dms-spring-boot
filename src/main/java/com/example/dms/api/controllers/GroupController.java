package com.example.dms.api.controllers;

import com.example.dms.api.dtos.SortDTO;
import com.example.dms.api.dtos.group.DmsGroupDTO;
import com.example.dms.api.dtos.group.NewGroupDTO;
import com.example.dms.api.dtos.user.DmsUserDTO;
import com.example.dms.services.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class GroupController {

	private final GroupService groupService;

	@GetMapping
	public List<DmsGroupDTO> getAllGroups(@RequestParam(required = false) String search, SortDTO sort) {
		return groupService.searchAll(search, sort);
	}

	@GetMapping("/{id}")
	public DmsGroupDTO getGroupById(@PathVariable Integer id) {
		return groupService.findById(id);
	}

	@GetMapping("/members/{id}")
	public Set<DmsUserDTO> getMembers(@PathVariable Integer id) {
		return groupService.findById(id).getMembers();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public DmsGroupDTO createNewGroup(@RequestBody @Valid NewGroupDTO groupDTO) {
		return groupService.createGroup(groupDTO);
	}


	@PutMapping("/{id}")
	public DmsGroupDTO updateGroup(@PathVariable Integer id, @RequestBody @Valid NewGroupDTO groupDTO) {
		return groupService.updateGroup(id, groupDTO);
	}

	@DeleteMapping("/{id}")
	public void deleteGroupById(@PathVariable Integer id) {
		groupService.deleteById(id);
	}

	@PostMapping("/members/{id}")
	public DmsGroupDTO addUsersToGroup(@PathVariable Integer id, @RequestBody List<Integer> idList) {
		return groupService.updateGroupMembers(id, idList);
	}
}
