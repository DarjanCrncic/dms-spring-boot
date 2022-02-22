package com.example.dms.services.integration.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;

import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.services.DmsAclService;
import com.example.dms.services.DocumentService;

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
	
//  DEBUGGING WITH H2 
//	@Autowired
//	DataSource dataSource;
//	@BeforeAll
//	public void initTest() throws SQLException {
//	    Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082")
//	    .start();
//	}
	
	DmsDocumentDTO newDocument;
	
	@BeforeEach
	void setUp() {
		newDocument = documentService.createNewDocument(
				NewDocumentDTO.builder().objectName("TestTest").description("Ovo je test u testu").build());
	}
	
	@AfterEach
	void cleanUp() {
		if (newDocument != null && documentRepository.existsById(newDocument.getId())) {
			documentRepository.deleteById(newDocument.getId());
		}
	}
	
	@Test
	@WithUserDetails("creator")
	void testModifyWithAcl() {
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		DmsDocumentDTO updatedDocument = documentService.updateDocument(newDocument.getId(), modifyDTO, true);
		
		assertEquals(modifyDTO.getObjectName(), updatedDocument.getObjectName());
	}
	
	@Test
	@WithMockUser(username = "testUser", authorities = {"CREATE_PRIVILEGE","ROLE_ADMIN"})
	void testModifyWithAclWithAdmin() {
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		DmsDocumentDTO updatedDocument = documentService.updateDocument(newDocument.getId(), modifyDTO, true);
		
		assertEquals(modifyDTO.getObjectName(), updatedDocument.getObjectName());
	}
	
	@Test
	@WithMockUser(username = "testUser", roles = "USER", authorities = "CREATE_PRIVILEGE")
	void testModifyWithAclInvalidUser() {
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		UUID docId = newDocument.getId();
		assertThrows(AccessDeniedException.class, () -> documentService.updateDocument(docId, modifyDTO, true));
	}
	
	@Test
	@WithMockUser(username = "testUser", roles = "USER", authorities = "CREATE_PRIVILEGE")
	void testModifyWithGrantedRights() {
		dmsAclService.grantRightsOnDocument(newDocument.getId(), (new PrincipalSid("testUser")), Arrays.asList(BasePermission.WRITE));
		
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		DmsDocumentDTO updatedDocument = documentService.updateDocument(newDocument.getId(), modifyDTO, true);
		assertEquals(modifyDTO.getObjectName(), updatedDocument.getObjectName());
	}
	
	@Test
	@WithMockUser(username = "testUser", roles = "USER", authorities = "CREATE_PRIVILEGE")
	void testModifyWithRevokedRights() {
		dmsAclService.grantRightsOnDocument(newDocument.getId(), (new PrincipalSid("testUser")), Arrays.asList(BasePermission.WRITE));
		dmsAclService.revokeRightsOnDocument(newDocument.getId(), (new PrincipalSid("testUser")), Arrays.asList(BasePermission.WRITE));
		
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		UUID docId = newDocument.getId();
		assertThrows(AccessDeniedException.class, () -> documentService.updateDocument(docId, modifyDTO, true));
	}
	
	@Test
	@WithMockUser(username = "testUser", roles = "USER", authorities = "CREATE_PRIVILEGE")
	void testModifyWithRevokedAllRights() {
		dmsAclService.grantRightsOnDocument(newDocument.getId(), (new PrincipalSid("testUser")), Arrays.asList(BasePermission.WRITE, BasePermission.READ));
		dmsAclService.revokeRightsOnDocument(newDocument.getId(), (new PrincipalSid("testUser")), null);
		
		ModifyDocumentDTO modifyDTO = ModifyDocumentDTO.builder().objectName("TestTestTest").build();
		UUID docId = newDocument.getId();
		assertThrows(AccessDeniedException.class, () -> documentService.updateDocument(docId, modifyDTO, true));
	}
}
