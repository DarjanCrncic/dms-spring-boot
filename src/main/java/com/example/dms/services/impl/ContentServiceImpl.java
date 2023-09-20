package com.example.dms.services.impl;

import com.example.dms.domain.DmsContent;
import com.example.dms.domain.DmsDocument;
import com.example.dms.repositories.ContentRepository;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.services.ContentService;
import com.example.dms.services.DocumentService;
import com.example.dms.utils.exceptions.BadRequestException;
import com.example.dms.utils.exceptions.DmsNotFoundException;
import com.example.dms.utils.exceptions.InternalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentServiceImpl implements ContentService {

	private final DocumentService documentService;
	private final ContentRepository contentRepository;
	private final DocumentRepository documentRepository;

	@Override
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','WRITE') || hasAuthority('WRITE_PRIVILEGE')")
	public void uploadFile(Integer id, MultipartFile file) {
		DmsDocument doc = documentRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		if (doc.isImmutable()) {
			throw new BadRequestException("Object is immutable and you cannot add content to it.");
		}
		if (doc.getContent() != null) {
			throw new BadRequestException("Object already has content.");
			// TODO create new version?
		}

		try {
			DmsContent content = DmsContent.builder().content(file.getBytes()).contentSize(file.getSize())
					.contentType(file.getContentType()).originalFileName(file.getOriginalFilename()).document(doc)
					.build();
			contentRepository.save(content);
			doc.setContent(content);
		} catch (IOException e) {
			throw new InternalException(
					"Could not upload file for document: '" + id + "'.");
		}
		documentService.save(doc);
	}

	@Override
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','READ') || hasAuthority('READ_PRIVILEGE')")
	public ResponseEntity<byte[]> downloadContent(Integer id) {
		DmsDocument document = documentRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		checkIsDocumentValidForDownload(document);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + document.getContent().getOriginalFileName() + "\"")
				.contentType(MediaType.valueOf(document.getContent().getContentType()))
				.header("File-Name", document.getContent().getOriginalFileName())
				.body(document.getContent().getContent());
	}

	private void checkIsDocumentValidForDownload(DmsDocument document) {
		if (document.getContent() == null ||
				document.getContent().getContentType() == null ||
				document.getContent().getOriginalFileName() == null ||
				document.getContent().getContentSize() == 0) {
			throw new InternalException("Document has corrupted (or no) content, download unavailable.");
		}
	}
}
