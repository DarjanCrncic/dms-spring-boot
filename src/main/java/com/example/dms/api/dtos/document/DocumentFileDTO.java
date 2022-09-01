package com.example.dms.api.dtos.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentFileDTO {
	
	private UUID id;
	private String urlToFile;
	private String contentType;
	private long contentSize;
	private String originalFileName;
	
}
