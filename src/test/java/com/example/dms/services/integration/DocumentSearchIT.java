package com.example.dms.services.integration;

import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.api.mappers.DocumentMapper;
import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsType;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.repositories.TypeRepository;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.DocumentService;
import com.example.dms.services.UserService;
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
	DocumentMapper documentMapper;

	@Autowired
	DocumentService documentService;

	@Autowired
	UserService userService;

	@Autowired
	DocumentRepository documentRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	TypeRepository typeRepository;

	DmsDocumentDTO newDocument, newDocument2, newDocument3;
	DmsDocumentDTO newVersion;
	DmsDocumentDTO updatedDocument;
	DmsType type;
	String typeName = "testni-tip";

	@BeforeEach
	void setUp() {
		type = typeRepository.save(DmsType.builder().typeName(typeName).build());
		newDocument = documentService.createDocument(NewDocumentDTO.builder().objectName("TestTest")
				.description("Ovo je test u testu").type(typeName).username("user").build());
		newDocument2 = documentService.createDocument(NewDocumentDTO.builder().objectName("Test2")
				.description("Ovo je test u testu 2").type(typeName).username("user").build());
		newDocument3 = documentService.createDocument(NewDocumentDTO.builder().objectName("Test3")
				.description("Ovo je test u testu 3").type(typeName).username("user").build());
	}

	@AfterEach
	void cleanUp() {
		if (newDocument != null && documentRepository.existsById(newDocument.getId()))
			documentRepository.deleteById(newDocument.getId());
		if (newDocument2 != null && documentRepository.existsById(newDocument2.getId()))
			documentRepository.deleteById(newDocument2.getId());
		if (newDocument3 != null && documentRepository.existsById(newDocument3.getId()))
			documentRepository.deleteById(newDocument3.getId());
		if (newVersion != null && documentRepository.existsById(newVersion.getId()))
			documentRepository.deleteById(newVersion.getId());
		if (updatedDocument != null && documentRepository.existsById(updatedDocument.getId()))
			documentRepository.deleteById(updatedDocument.getId());
		if (type != null && typeRepository.existsById(type.getId()))
			typeRepository.delete(type);
	}

	@Test
	@DisplayName("Test search with search criteria.")
	void testSearchCriteria() {
		DocumentSpecification spec1 = new DocumentSpecification(
				new SearchCriteria("object_name", ":", "TestTest"));
		DocumentSpecification spec2 = new DocumentSpecification(new SearchCriteria("creator", ":", "user"));

		assertEquals(1, documentRepository.findAll(Specification.where(spec1).and(spec2)).size());
	}

	@Test
	@DisplayName("Test search with search criteria - no results found.")
	void testSearchCriteriaNotFound() {
		DocumentSpecification spec1 = new DocumentSpecification(
				new SearchCriteria("object_name", ":", "TestTestaaa"));
		DocumentSpecification spec2 = new DocumentSpecification(
				new SearchCriteria("description", ":", "testaaaaa"));

		assertEquals(0, documentRepository.findAll(Specification.where(spec1).and(spec2)).size());
	}

	@Test
	@DisplayName("Test specification builder.")
	void testSpecificationBuilder() {
		SpecificationBuilder<DmsDocument> builder = new SpecificationBuilder<>(new DocumentSpecProvider());
		Specification<DmsDocument> spec = builder.parse("object_name:TestTest,creator:user");

		assertEquals(1, documentRepository.findAll(spec).size());
	}

	@Test
	@DisplayName("Test specification builder with more documents.")
	void testSpecificationBuilderMoreDocuments() {
		SpecificationBuilder<DmsDocument> builder = new SpecificationBuilder<>(new DocumentSpecProvider());
		Specification<DmsDocument> spec = builder.parse("object_name:Test3,creator:user~object_name:Test2");

		assertEquals(2, documentRepository.findAll(spec).size());
	}

	@Test
	@DisplayName("Test specification builder with type.")
	void testSpecificationBuilderType() {
		SpecificationBuilder<DmsDocument> builder = new SpecificationBuilder<>(new DocumentSpecProvider());
		Specification<DmsDocument> spec = builder.parse("type:testni-tip");

		assertEquals(3, documentRepository.findAll(spec).size());
	}
}
