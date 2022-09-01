package com.example.dms.services;

import com.example.dms.api.dtos.type.DmsTypeDTO;
import com.example.dms.api.dtos.type.NewTypeDTO;
import com.example.dms.domain.DmsType;

import java.util.UUID;

public interface TypeService extends CrudService<DmsType, DmsTypeDTO, UUID> {

	DmsTypeDTO createType(NewTypeDTO dmsTypeDTO);

}
