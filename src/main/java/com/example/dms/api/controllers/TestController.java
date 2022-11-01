package com.example.dms.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
public class TestController {

	@GetMapping
	@ResponseStatus(value = HttpStatus.OK)
	public String testGetMapping() {
		return "Success";
	}
	
	@GetMapping("/error")
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public String testError() {
		return "Error";
	}
	
	@PostMapping
	@ResponseStatus(value = HttpStatus.OK)
	public String testPostMapping() {
		return "Success";
	}

}
