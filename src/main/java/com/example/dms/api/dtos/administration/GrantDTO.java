package com.example.dms.api.dtos.administration;

import java.util.Set;

import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GrantDTO {
	
	private String username;
	
	@Pattern(regexp = "^(READ|WRITE|CREATE|DELETE|ADMINISTRATION)!", message = "Ivalid permission.")
	private Set<String> permissions;
}
