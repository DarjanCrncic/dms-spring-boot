package com.example.dms.api.dtos.document;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.dms.api.dtos.user.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDTO {

	private UUID id;
	private String objectName;
	private String description;
	private UserDTO creator;
	
	private Long contentSize;
	private String contentType;
	private String originalFileName;
	
	private LocalDateTime creationDate;
	private LocalDateTime modifyDate;
}
