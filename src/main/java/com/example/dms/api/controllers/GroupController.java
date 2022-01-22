package com.example.dms.api.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.dms.api.dtos.group.NewGroupDTO;
import com.example.dms.api.mappers.GroupMapper;
import com.example.dms.domain.DmsGroup;
import com.example.dms.services.GroupService;
import com.example.dms.utils.exceptions.BadRequestException;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {

	@Autowired
	GroupService groupService;
	
	@Autowired
	GroupMapper groupMapper;
	
	@GetMapping("/")
	public List<DmsGroup> getAllGroups() {
		return groupService.findAll();
	}
	
	@GetMapping("/search") 
	public DmsGroup getGroupByReqParam(@RequestParam Optional<String> name) {
		if (name.isPresent()) {
			return groupService.findGroupByGroupName(name.get());
		}
		throw new BadRequestException("Request prameters for search are invalid.");
	}
	
	@GetMapping("/{id}")
	public DmsGroup getGroupById(@PathVariable UUID id) {
		return groupService.findById(id);
	}
	
	@PostMapping("/") 
	@ResponseStatus(HttpStatus.CREATED)
	public DmsGroup createNewGroup(@RequestBody @Valid NewGroupDTO groupDTO){
		return groupService.createNewGroup(groupMapper.newGroupDtoToGroup(groupDTO));
	}
	

	@PutMapping("/{id}")
	public DmsGroup updateGroup(@PathVariable UUID id, @RequestBody @Valid NewGroupDTO groupDTO) {
		return groupService.updateGroup(id, groupDTO); 
	}
	
	@DeleteMapping("/{id}")
	public void deleteGroupById(@PathVariable UUID id) {
		groupService.deleteById(id);
	}
	
	@PostMapping("/users/{id}")
	public DmsGroup addUsersToGroup(@PathVariable UUID id, @RequestBody List<UUID> idList) {
		return groupService.addUsersToGroup(id, idList);
	}
}
