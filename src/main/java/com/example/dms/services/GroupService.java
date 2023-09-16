package com.example.dms.services;

import com.example.dms.api.dtos.SortDTO;
import com.example.dms.api.dtos.group.DmsGroupDTO;
import com.example.dms.api.dtos.group.NewGroupDTO;
import com.example.dms.domain.DmsGroup;

import javax.validation.Valid;
import java.util.List;

public interface GroupService extends CrudService<DmsGroup, DmsGroupDTO, Integer>{

	DmsGroupDTO createGroup(@Valid NewGroupDTO groupDTO);

	DmsGroupDTO addUserToGroup(Integer groupId, Integer userId);

	DmsGroupDTO updateGroupMembers(Integer groupId, List<Integer> userList);

	DmsGroupDTO updateGroup(Integer id, NewGroupDTO groupDTO);

	List<DmsGroupDTO> searchAll(String search, SortDTO sort);
}
