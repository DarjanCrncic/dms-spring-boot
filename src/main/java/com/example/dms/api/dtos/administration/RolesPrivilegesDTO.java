package com.example.dms.api.dtos.administration;

import lombok.Data;

import java.util.List;

@Data
public class RolesPrivilegesDTO {
	private final List<String> roles;
	private final List<String> privileges;
}
