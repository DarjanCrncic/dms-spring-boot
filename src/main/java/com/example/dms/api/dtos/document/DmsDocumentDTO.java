package com.example.dms.api.dtos.document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.dms.api.dtos.user.DmsUserDTO;
import com.example.dms.domain.DmsFolder;
import com.example.dms.domain.DmsType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DmsDocumentDTO {

	private UUID id;
	private LocalDateTime creationDate;
	private LocalDateTime modifyDate;

	private String objectName;
	private String description;
	private DmsUserDTO creator;
	private DmsType type;
	@Default
	private List<String> keywords = new ArrayList<>();
	private DmsFolder parentFolder;
	
	private Long contentSize;
	private String contentType;
	private String originalFileName;
	
	private UUID rootId;
	private UUID predecessorId;
	private boolean imutable;
	private int version;
}
