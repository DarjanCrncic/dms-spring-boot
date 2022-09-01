package com.example.dms.utils.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@NoArgsConstructor
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class DmsNotFoundException extends RuntimeException{
	
	public DmsNotFoundException(@NonNull String message) {
		super();
		this.message = message;
	}

	private static final long serialVersionUID = -7305813945482066539L;
	
	@NonNull
	private String message = "Entity with specified id could not be found.";
	
}
