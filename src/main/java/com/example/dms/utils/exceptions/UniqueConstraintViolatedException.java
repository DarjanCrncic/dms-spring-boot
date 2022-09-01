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
public class UniqueConstraintViolatedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5580241064289440114L;

	public UniqueConstraintViolatedException(@NonNull String message) {
		super();
		this.message = message;
	}

	@NonNull
	private String message = "Unique constraint violated.";
}
