package com.example.dms.api.dtos.user;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
	
	private UUID id;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	
	private LocalDateTime creationDate;
	private LocalDateTime modifyDate;
}
