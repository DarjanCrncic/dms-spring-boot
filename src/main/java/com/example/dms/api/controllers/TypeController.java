package com.example.dms.api.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dms.api.dtos.type.DmsTypeDTO;
import com.example.dms.api.dtos.type.NewTypeDTO;
import com.example.dms.services.TypeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/types")
@RequiredArgsConstructor
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
