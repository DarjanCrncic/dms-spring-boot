package com.example.dms.api.dtos.document;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class CopyDocumentsDTO {

	List<UUID> documents;
	UUID folderId;
}
