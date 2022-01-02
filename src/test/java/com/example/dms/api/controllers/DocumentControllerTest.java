package com.example.dms.api.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.dms.api.dtos.document.DocumentDTO;
import com.example.dms.api.mappers.DocumentMapper;
import com.example.dms.domain.Document;
import com.example.dms.domain.User;

@SpringBootTest
class DocumentControllerTest {

	@Autowired
	DocumentMapper documentMapper;
	
	@Test
	void testDocumentToDocumentDTOMapping() {
		User user = new User("dcrncic", "12345", "Darjan", "Crnčić","darjan.crncic@gmail.com");	
    	
    	Document doc1 = new Document(user, "test1");
    	DocumentDTO docDTO = documentMapper.documentToDocumentDTO(doc1);
    	
    	assertEquals(doc1.getObjectName(), docDTO.getObjectName());
	}
}
