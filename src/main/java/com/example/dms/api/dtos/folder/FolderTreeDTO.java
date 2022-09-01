package com.example.dms.api.dtos.folder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FolderTreeDTO {

	private UUID id;
	private String name;
	private UUID parentFolderId;
	private int numOfDocuments;
}
