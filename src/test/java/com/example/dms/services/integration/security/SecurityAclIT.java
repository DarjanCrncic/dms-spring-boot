package com.example.dms.services.integration.security;

import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsFolder;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.services.DmsAclService;
import com.example.dms.services.DocumentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ContextConfiguration
class SecurityAclIT {

	@Autowired
	DocumentService documentService;

	@Autowired
	DmsAclService dmsAclService;
	
	@Autowired
	DocumentRepository documentRepository;
	
	@Autowired
	JdbcMutableAclService aclService;

	@Autowired
	FolderRepository folderRepository;

	DmsDocumentDTO newDocument;
	DmsDocument doc;
	private final String username = "tester";
	
	@BeforeEach
	void setUp() {
		DmsFolder root = folderRepository.findByName("/").orElse(null);
		assert root != null;
		newDocument = documentService.createDocument(
				NewDocumentDTO.builder()
						.objectName("TestTest")
						.description("Ovo je test u testu")
						.type("document")
						.parentFolderId(root.getId()).build());
		doc = documentRepository.findById(newDocument.getId()).orElse(null);
	}
	
	@AfterEach
	void cleanUp() {
		if (newDocument != null && documentRepository.existsById(newDocument.getId())) {
			dmsAclService.removeEntriesOnDelete(doc);
			documentRepository.deleteById(newDocument.getId());
		}
	}
	
	@Test
	@WithMockUser(username = "user", authorities = {"CREATE_PRIVILEGE"})
	void testModifyWithAclWithAdmin() {
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		DmsDocumentDTO updatedDocument = documentService.updateDocument(newDocument.getId(), modifyDTO, true);
		
		assertEquals(modifyDTO.getObjectName(), updatedDocument.getObjectName());
	}

	@Test
	@WithMockUser(username = username, roles = "USER", authorities = {"CREATE_PRIVILEGE"})
	void testModifyWithGrantedRights() {
		dmsAclService.revokeRightsOnObject(doc, (new PrincipalSid(username)), List.of(BasePermission.WRITE));
		dmsAclService.grantRightsOnObject(doc, (new PrincipalSid(username)), List.of(BasePermission.WRITE));
		
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		DmsDocumentDTO updatedDocument = documentService.updateDocument(newDocument.getId(), modifyDTO, true);

		assertEquals(modifyDTO.getObjectName(), updatedDocument.getObjectName());
	}
	
	@Test
	@WithMockUser(username = username, roles = "USER", authorities = "CREATE_PRIVILEGE")
	void testModifyWithRevokedRights() {
		dmsAclService.revokeRightsOnObject(doc, (new PrincipalSid(username)), List.of(BasePermission.WRITE));
		
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		Integer docId = newDocument.getId();

		assertThrows(AccessDeniedException.class, () -> documentService.updateDocument(docId, modifyDTO, true));
	}
	
	@Test
	@WithMockUser(username = username, roles = "USER", authorities = "CREATE_PRIVILEGE")
	void testModifyWithRevokedAllRights() {
		dmsAclService.revokeRightsOnObject(doc, (new PrincipalSid(username)), null);
		
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		Integer docId = newDocument.getId();
		assertThrows(AccessDeniedException.class, () -> documentService.updateDocument(docId, modifyDTO, true));
	}
	@Test
	@WithMockUser(username = username, authorities = {"ROLE_ADMIN", "CREATE_PRIVILEGE"})
	void testRemoveAclsManually() {
		dmsAclService.revokeRightsOnObject(doc, (new PrincipalSid(username)), null);
		
		MutableAcl acl = (MutableAcl) aclService.readAclById(new ObjectIdentityImpl(doc));
		assertFalse(dmsAclService.hasRight(doc, username, List.of(BasePermission.WRITE)));
		assertFalse(dmsAclService.hasRight(doc, username, List.of(BasePermission.READ)));
	}
	
	@Test
	@WithMockUser(username = username, authorities = {"ROLE_ADMIN", "CREATE_PRIVILEGE"})
	void testRemoveAclsOnDelete() {
		documentService.deleteById(doc.getId());
		
		assertThrows(NotFoundException.class, () -> aclService.readAclById(new ObjectIdentityImpl(doc)));
	}
}
