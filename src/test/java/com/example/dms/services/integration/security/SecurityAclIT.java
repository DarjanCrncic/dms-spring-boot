package com.example.dms.services.integration.security;

import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.DmsDocument;
import com.example.dms.repositories.DocumentRepository;
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

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ContextConfiguration
//@TestInstance(Lifecycle.PER_CLASS) // DEBUGGING WITH H2 
class SecurityAclIT {

	@Autowired
	DocumentService documentService;

	@Autowired
	DmsAclService dmsAclService;
	
	@Autowired
	DocumentRepository documentRepository;
	
	@Autowired
	JdbcMutableAclService aclService;
	
//  DEBUGGING WITH H2 
//	@Autowired
//	DataSource dataSource;
//	@BeforeAll
//	public void initTest() throws SQLException {
//	    Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082")
//	    .start();
//	}
	
	DmsDocumentDTO newDocument;
	DmsDocument doc;
	private final String username = "testUser";
	
	@BeforeEach
	void setUp() {
		newDocument = documentService.createDocument(
				NewDocumentDTO.builder().objectName("TestTest").description("Ovo je test u testu").username("user").type("document").build());
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
	@WithMockUser(username = "user", roles = "USER", authorities = "CREATE_PRIVILEGE")
	void testModifyWithAclInvalidUser() {
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		
		DmsDocumentDTO updatedDocument = documentService.updateDocument(newDocument.getId(), modifyDTO, true);
		assertEquals(modifyDTO.getObjectName(), updatedDocument.getObjectName());
	}
	
	@Test
	@WithMockUser(username = username, roles = "USER", authorities = {"CREATE_PRIVILEGE"})
	void testModifyWithGrantedRights() {
		dmsAclService.grantRightsOnObject(doc, (new PrincipalSid(username)), Arrays.asList(BasePermission.WRITE));
		
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		DmsDocumentDTO updatedDocument = documentService.updateDocument(newDocument.getId(), modifyDTO, true);
		assertEquals(modifyDTO.getObjectName(), updatedDocument.getObjectName());
	}
	
	@Test
	@WithMockUser(username = username, roles = "USER", authorities = "CREATE_PRIVILEGE")
	void testModifyWithRevokedRights() {
		dmsAclService.grantRightsOnObject(doc, (new PrincipalSid(username)), Arrays.asList(BasePermission.WRITE));
		dmsAclService.revokeRightsOnObject(doc, (new PrincipalSid(username)), Arrays.asList(BasePermission.WRITE));
		
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		UUID docId = newDocument.getId();
		assertThrows(AccessDeniedException.class, () -> documentService.updateDocument(docId, modifyDTO, true));
	}
	
	@Test
	@WithMockUser(username = username, roles = "USER", authorities = "CREATE_PRIVILEGE")
	void testModifyWithRevokedAllRights() {
		dmsAclService.grantRightsOnObject(doc, (new PrincipalSid(username)), Arrays.asList(BasePermission.WRITE, BasePermission.READ));
		dmsAclService.revokeRightsOnObject(doc, (new PrincipalSid(username)), null);
		
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		UUID docId = newDocument.getId();
		assertThrows(AccessDeniedException.class, () -> documentService.updateDocument(docId, modifyDTO, true));
	}
	@Test
	@WithMockUser(username = username, authorities = {"ROLE_ADMIN", "CREATE_PRIVILEGE"})
	void testDeleteingAcls() {
		dmsAclService.grantRightsOnObject(doc, (new PrincipalSid(username)), Arrays.asList(BasePermission.WRITE, BasePermission.READ));
		dmsAclService.revokeRightsOnObject(doc, (new PrincipalSid(username)), null);
		
		MutableAcl acl = (MutableAcl) aclService.readAclById(new ObjectIdentityImpl(doc));
		assertEquals(4, acl.getEntries().size());
	}
	
	@Test
	@WithMockUser(username = username, authorities = {"ROLE_ADMIN", "CREATE_PRIVILEGE"})
	void testDeleteingAclsOnDelete() {
		dmsAclService.grantRightsOnObject(doc, (new PrincipalSid("testUser")), Arrays.asList(BasePermission.WRITE, BasePermission.READ));
		documentService.deleteById(doc.getId());
		
		assertThrows(NotFoundException.class, () -> aclService.readAclById(new ObjectIdentityImpl(doc)));
	}
}
