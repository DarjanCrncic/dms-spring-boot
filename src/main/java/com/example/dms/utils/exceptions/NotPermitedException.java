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
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class NotPermitedException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 24446889163463568L;

	public NotPermitedException(@NonNull String message) {
		super();
		this.message = message;
	}

	@NonNull
	private String message = "You don't have permissions for this action.";
}
