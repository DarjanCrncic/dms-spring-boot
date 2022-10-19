package com.example.dms.api.mappers;

import com.example.dms.api.dtos.group.DmsGroupDTO;
import com.example.dms.api.dtos.group.NewGroupDTO;
import com.example.dms.domain.DmsGroup;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GroupMapper extends MapperInterface<DmsGroup, DmsGroupDTO>{

	GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);
	
	@Mapping(target = "members", ignore = true)
	DmsGroup newGroupDtoToGroup(NewGroupDTO groupDTO);

	void updateGroupPut(NewGroupDTO groupDTO, @MappingTarget DmsGroup group);
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateGroupPatch(NewGroupDTO groupDTO, @MappingTarget DmsGroup group);
}
