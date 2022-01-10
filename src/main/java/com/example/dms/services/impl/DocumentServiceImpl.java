package com.example.dms.services.impl;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.Document;
import com.example.dms.services.DocumentService;
import com.example.dms.services.UserService;
import com.example.dms.utils.exceptions.InternalException;

@Service
public class DocumentServiceImpl extends EntityCrudServiceImpl<Document> implements DocumentService{

	@Autowired
	UserService userService;
	
	@Override
	public Document createNewDocument(NewDocumentDTO newDocumentDTO) {
		// TODO: Refactor to use mapper instead of creating a new instance.
		// TODO: Remove hardcoded user.
		Document newDocumentObject = new Document(userService.findByUsername("dcrncic"), newDocumentDTO.getObjectName());
		return save(newDocumentObject);
	}

	@Override
	public void uploadFile(UUID id, MultipartFile file) {
		Document doc = findById(id);
		
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
	public boolean checkIsDocumentValidForDownload(Document document) {
		if (document.getContent() == null || document.getContentType() == null || document.getOriginalFileName() == null) 
			throw new InternalException("Document has corrupted (or no) content, download unavailable.");
		return true;
	}
	
	

}
