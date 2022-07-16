package com.example.dms.api.dtos.folder;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FolderTreeDTO {

	private UUID id;
	private String path;
	private String parentFolder;
	private int numOfDocuments;
}
