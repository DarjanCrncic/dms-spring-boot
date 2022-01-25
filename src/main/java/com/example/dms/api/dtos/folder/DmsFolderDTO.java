package com.example.dms.api.dtos.folder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsFolder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DmsFolderDTO {

	private UUID id;
	private LocalDateTime creationDate;
	private LocalDateTime modifyDate;
	
	private String path;
	private DmsFolder parentFolder;
	private List<DmsFolder> subfolders;
	private List<DmsDocument> documents;
}
