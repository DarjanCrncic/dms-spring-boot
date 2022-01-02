package com.example.dms.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.Document;
import com.example.dms.services.DocumentService;
import com.example.dms.services.UserService;

@Service
public class DocumentServiceImpl extends EntityCrudServiceImpl<Document> implements DocumentService{

	@Autowired
	UserService userService;
	
	@Override
	public Document createNewDocument(NewDocumentDTO newDocumentDTO) {
		Document newDocumentObject = new Document();
		newDocumentObject.setCreator(userService.findByUsername("dcrncic"));
		newDocumentObject.setObjectName(newDocumentDTO.getObjectName());
		return this.save(newDocumentObject);
	}

}
