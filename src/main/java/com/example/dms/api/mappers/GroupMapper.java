package com.example.dms.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.dms.api.dtos.group.DmsGroupDTO;
import com.example.dms.api.dtos.group.NewGroupDTO;
import com.example.dms.domain.DmsGroup;

@Mapper
public interface GroupMapper extends MapperInterface<DmsGroup, DmsGroupDTO>{

	GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);
	
	@Mapping(target = "members", ignore = true)
	public DmsGroup newGroupDtoToGroup(NewGroupDTO groupDTO);
}
