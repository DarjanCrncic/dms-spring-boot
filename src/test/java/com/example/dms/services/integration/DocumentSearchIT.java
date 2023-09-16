package com.example.dms.services.integration;

import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsFolder;
import com.example.dms.domain.DmsType;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.repositories.TypeRepository;
import com.example.dms.services.DocumentService;
import com.example.dms.services.search.SearchCriteria;
import com.example.dms.services.search.SpecificationBuilder;
import com.example.dms.services.search.document.DocumentSpecProvider;
import com.example.dms.services.search.document.DocumentSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration
@WithMockUser(username = "user", authorities = {"CREATE_PRIVILEGE", "READ_PRIVILEGE", "WRITE_PRIVILEGE"})
class DocumentSearchIT {

	@Autowired
	DocumentService documentService;

	@Autowired
	DocumentRepository documentRepository;

	@Autowired
	FolderRepository folderRepository;

	@Autowired
	TypeRepository typeRepository;

	DmsDocumentDTO newDocument, newDocument2, newDocument3;
	DmsType type;
	String typeName = "testni-tip";

	@BeforeEach
	void setUp() {
		type = typeRepository.save(DmsType.builder().typeName(typeName).build());
		DmsFolder root = folderRepository.findByName("/").orElse(null);
		assert root != null;

		newDocument = documentService.createDocument(generateNewDocumentDTO(
				"Test1", "Ovo je test u testu 1", typeName, root.getId()));
		newDocument2 = documentService.createDocument(generateNewDocumentDTO(
				"Test2", "Ovo je test u testu 2", typeName, root.getId()));
		newDocument3 = documentService.createDocument(generateNewDocumentDTO(
				"Test3", "Ovo je test u testu 3", typeName, root.getId()));
	}

	private NewDocumentDTO generateNewDocumentDTO(String objectName, String description, String typeName, Integer parentFolderId) {
		return NewDocumentDTO.builder()
				.objectName(objectName)
				.description(description)
				.type(typeName)
				.parentFolderId(parentFolderId)
				.build();
	}

	@AfterEach
	void cleanUp() {
		documentRepository.deleteAll();
		typeRepository.deleteById(type.getId());
	}

	@Test
	@DisplayName("Test search with search criteria.")
	void testSearchCriteria() {
		DocumentSpecification spec1 = new DocumentSpecification(
				new SearchCriteria("objectName", "LIKE", "Test"));
		DocumentSpecification spec2 = new DocumentSpecification(
				new SearchCriteria("creator", "LIKE", "user"));

		assertEquals(3, documentRepository.findAll(Specification.where(spec1).and(spec2)).size());
	}

	@Test
	@DisplayName("Test search with search criteria - no results found.")
	void testSearchCriteriaNotFound() {
		DocumentSpecification spec1 = new DocumentSpecification(
				new SearchCriteria("objectName", "LIKE", "TestTestaaa"));
		DocumentSpecification spec2 = new DocumentSpecification(
				new SearchCriteria("description", "LIKE", "testaaaaa"));

		assertEquals(0, documentRepository.findAll(Specification.where(spec1).and(spec2)).size());
	}

	@Test
	@DisplayName("Test specification builder.")
	void testSpecificationBuilder() {
		SpecificationBuilder<DmsDocument> builder = new SpecificationBuilder<>(new DocumentSpecProvider());
		Specification<DmsDocument> spec = builder.parse("objectName~like~Test1~and~creator~like~user");

		assertEquals(1, documentRepository.findAll(spec).size());
	}

	@Test
	@DisplayName("Test specification builder with more documents.")
	void testSpecificationBuilderMoreDocuments() {
		SpecificationBuilder<DmsDocument> builder = new SpecificationBuilder<>(new DocumentSpecProvider());
		Specification<DmsDocument> spec = builder.parse("objectName~like~Test3~and~creator~like~user~or~objectName~like~Test2");

		assertEquals(2, documentRepository.findAll(spec).size());
	}

	@Test
	@DisplayName("Test specification builder with type.")
	void testSpecificationBuilderType() {
		SpecificationBuilder<DmsDocument> builder = new SpecificationBuilder<>(new DocumentSpecProvider());
		Specification<DmsDocument> spec = builder.parse("type~eq~testni-tip");

		assertEquals(3, documentRepository.findAll(spec).size());
	}
}
