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
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6470159231985984338L;

	public InternalException(@NonNull String message) {
		super();
		this.message = message;
	}

	@NonNull
	private String message = "Internal server error.";
}
