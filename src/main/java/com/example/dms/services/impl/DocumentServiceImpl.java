package com.example.dms.services.impl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.DmsDocument;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.services.DocumentService;
import com.example.dms.services.UserService;
import com.example.dms.utils.exceptions.BadRequestException;
import com.example.dms.utils.exceptions.InternalException;

@Service
@Transactional
public class DocumentServiceImpl extends EntityCrudServiceImpl<DmsDocument> implements DocumentService{

	UserService userService;
	DocumentRepository documentRepository;

	public DocumentServiceImpl(UserService userService, DocumentRepository documentRepository) {
		super();
		this.userService = userService;
		this.documentRepository = documentRepository;
	}

	@Override
	public DmsDocument createNewDocument(NewDocumentDTO newDocumentDTO) {
		// TODO: Refactor to use mapper instead of creating a new instance.
		// TODO: Remove hardcoded user.
		DmsDocument newDocumentObject = DmsDocument.builder().creator(userService.findByUsername("dcrncic"))
				.objectName(newDocumentDTO.getObjectName()).build();
		DmsDocument savedDocumentObject = save(newDocumentObject);
		savedDocumentObject.setRootId(savedDocumentObject.getId());
		savedDocumentObject.setPredecessorId(savedDocumentObject.getId());
		return save(savedDocumentObject);
	}

	@Override
	public void uploadFile(UUID id, MultipartFile file) {
		DmsDocument doc = findById(id);
		
		String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			doc.setContent(file.getBytes());
		} catch (IOException e) {
			throw new InternalException("Could not upload file: '" + originalFileName + "' for document: '" + id + "'.");
		}
		doc.setContentSize(file.getSize());
		doc.setContentType(file.getContentType());
		doc.setOriginalFileName(originalFileName);
		save(doc);
	}

	@Override
	public boolean checkIsDocumentValidForDownload(DmsDocument document) {
		if (document.getContent() == null || document.getContentType() == null || document.getOriginalFileName() == null) 
			throw new InternalException("Document has corrupted (or no) content, download unavailable.");
		return true;
	}
	
	@Override
	public DmsDocument createNewVersion(UUID id) {
		DmsDocument doc = findById(id);
		if (doc.isImutable()) {
			throw new BadRequestException("This version of the document is immutable and cannot be versioned. "
					+ "Currently you can only version the latest version of the document.");
		}
			
		DmsDocument newVersion = copyDocument(doc);
		newVersion.setRootId(doc.getRootId());
		newVersion.setPredecessorId(doc.getId());
		newVersion.setVersion(doc.getVersion()+1);
		
		doc.setImutable(true);
		save(doc);
		
		return save(newVersion);
	}
	
	@Override
	public List<DmsDocument> getAllVersions(UUID id) {
		return documentRepository.getAllVersions(id);
	}
	
	private DmsDocument copyDocument(DmsDocument original) {
		return DmsDocument.builder()
				.content(original.getContent())
				.contentSize(original.getContentSize())
				.contentType(original.getContentType())
				.creator(original.getCreator())
				.description(original.getDescription())
				.parentFolder(original.getParentFolder())
				.objectName(original.getObjectName())
				.originalFileName(original.getOriginalFileName()).build();
	}
}
