package com.example.dms.services;

import com.example.dms.api.dtos.type.DmsTypeDTO;
import com.example.dms.api.dtos.type.NewTypeDTO;
import com.example.dms.domain.DmsType;

public interface TypeService extends CrudService<DmsType, DmsTypeDTO, Integer> {

	DmsTypeDTO createType(NewTypeDTO dmsTypeDTO);

}
