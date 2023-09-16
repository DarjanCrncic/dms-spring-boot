package com.example.dms.api.dtos.folder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FolderTreeDTO {

	private Integer id;
	private String name;
	private Integer parentFolderId;
	private int numOfDocuments;
}
