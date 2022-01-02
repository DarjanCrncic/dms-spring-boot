package com.example.dms.api.dtos.document;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class DocumentDTO {

	private UUID id;
	private String objectName;
	private LocalDateTime creationDate;
	private LocalDateTime modifyDate;
}
