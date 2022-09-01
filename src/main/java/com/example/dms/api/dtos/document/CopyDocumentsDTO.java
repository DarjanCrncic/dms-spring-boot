package com.example.dms.api.dtos.document;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CopyDocumentsDTO {

	List<UUID> documents;
	UUID folderId;
}
