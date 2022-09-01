package com.example.dms.api.dtos.administration;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.Set;

@Data
@AllArgsConstructor
public class GrantDTO {
	
	private String username;
	
	@Pattern(regexp = "^(READ|WRITE|CREATE|DELETE|ADMINISTRATION)!", message = "Ivalid permission.")
	private Set<String> permissions;
}
