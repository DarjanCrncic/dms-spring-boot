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
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException{

	private static final long serialVersionUID = 1903698637619143229L;

	public BadRequestException(@NonNull String message) {
		super();
		this.message = message;
	}

	@NonNull
	private String message = "Ivalid request params.";
}
