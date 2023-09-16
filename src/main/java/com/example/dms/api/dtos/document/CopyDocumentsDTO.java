package com.example.dms.api.dtos.document;

import lombok.Data;

import java.util.List;

@Data
public class CopyDocumentsDTO {

	List<Integer> documents;
	Integer folderId;
}
