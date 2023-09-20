package com.example.dms.api.controllers;

import com.example.dms.api.dtos.type.DmsTypeDTO;
import com.example.dms.api.dtos.type.NewTypeDTO;
import com.example.dms.services.TypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/types")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class TypeController {

	private final TypeService typeService;
	
	@GetMapping
	public List<DmsTypeDTO> getListOfTypes() {
		return typeService.findAll();
	}
	
	@PostMapping
	public DmsTypeDTO createNewType(@RequestBody @Valid NewTypeDTO newTypeDTO) {
		return typeService.createType(newTypeDTO);
	}
}
