package com.example.dms.api.dtos.user;

import com.example.dms.api.dtos.BaseEntityDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DmsUserDTO implements BaseEntityDTO {
	
	private UUID id;
	private LocalDateTime creationDate;
	private LocalDateTime modifyDate;

	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private boolean enabled;
	
}
