package com.example.dms.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException{
	
	public UserNotFoundException(@NonNull String message) {
		super();
		this.message = message;
	}

	private static final long serialVersionUID = -7305813945482066539L;
	
	@NonNull
	private String message = "User could not be found.";
	
}