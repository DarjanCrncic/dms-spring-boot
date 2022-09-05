package com.example.dms.services;

import com.example.dms.domain.DmsDocument;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ContentService {
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','WRITE') || hasAuthority('WRITE_PRIVILEGE')")
	void uploadFile(UUID id, MultipartFile file);

	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','READ') || hasAuthority('READ_PRIVILEGE')")
	ResponseEntity<byte[]> downloadContent(UUID id);

	@PreAuthorize("hasPermission(#document,'READ')")
	boolean checkIsDocumentValidForDownload(DmsDocument document);
}
