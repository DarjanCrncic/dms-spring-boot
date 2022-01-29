package com.example.dms.api.dtos.type;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DmsTypeDTO {

	private UUID id;
	private LocalDateTime creationDate;
	private LocalDateTime modifyDate;

	private String typeName;
}
