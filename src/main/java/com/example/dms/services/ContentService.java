package com.example.dms.services;

import com.example.dms.domain.DmsDocument;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

public interface ContentService {
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','WRITE') || hasAuthority('WRITE_PRIVILEGE')")
	void uploadFile(Integer id, MultipartFile file);

	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','READ') || hasAuthority('READ_PRIVILEGE')")
	ResponseEntity<byte[]> downloadContent(Integer id);

	@PreAuthorize("hasPermission(#document,'READ')")
	boolean checkIsDocumentValidForDownload(DmsDocument document);
}
