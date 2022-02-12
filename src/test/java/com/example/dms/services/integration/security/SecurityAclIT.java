package com.example.dms.services.integration.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;

import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.services.DocumentService;

@SpringBootTest
@ContextConfiguration
class SecurityAclIT {

	@Autowired
	DocumentService documentService;

	
	@Test
	@WithUserDetails("user")
	void testModifyWithAcl() {
		DmsDocumentDTO newDocument = documentService.createNewDocument(
				NewDocumentDTO.builder().objectName("TestTest").description("Ovo je test u testu").build());
		
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		DmsDocumentDTO updatedDocument = documentService.updateDocument(newDocument.getId(), modifyDTO, true);
		
		assertEquals(modifyDTO.getObjectName(), updatedDocument.getObjectName());
		assertNull(modifyDTO.getDescription());
		assertNull(modifyDTO.getKeywords());
		assertEquals(newDocument.getRootId(), updatedDocument.getRootId());
	}
	
	@Test
	@WithMockUser(username = "testUser", roles = "ADMIN")
	void testModifyWithAclWithAdmin() {
		DmsDocumentDTO newDocument = documentService.createNewDocument(
				NewDocumentDTO.builder().objectName("TestTest").description("Ovo je test u testu").build());
		
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		DmsDocumentDTO updatedDocument = documentService.updateDocument(newDocument.getId(), modifyDTO, true);
		
		assertEquals(modifyDTO.getObjectName(), updatedDocument.getObjectName());
		assertNull(modifyDTO.getDescription());
		assertNull(modifyDTO.getKeywords());
		assertEquals(newDocument.getRootId(), updatedDocument.getRootId());
	}
	
	@Test
	@WithMockUser(username = "testUser", roles = "USER")
	void testModifyWithAclInvalidUser() {
		DmsDocumentDTO newDocument = documentService.createNewDocument(
				NewDocumentDTO.builder().objectName("TestTest").description("Ovo je test u testu").build());
		
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		assertThrows(AccessDeniedException.class, () -> documentService.updateDocument(newDocument.getId(), modifyDTO, true));
	}
}
