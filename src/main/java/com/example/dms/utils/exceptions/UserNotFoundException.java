package com.example.dms.utils.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserNotFoundException extends RuntimeException{
	
	public UserNotFoundException(@NonNull String message) {
		super();
		this.message = message;
	}

	private static final long serialVersionUID = -7305813945482066539L;
	
	private final HttpStatus status = HttpStatus.NOT_FOUND;
	
	@NonNull
	private String message = "User could not be found.";
	
}
