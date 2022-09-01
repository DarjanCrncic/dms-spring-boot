package com.example.dms.api.controllers;

import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.api.dtos.type.DmsTypeDTO;
import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsType;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.UserRepository;
import com.example.dms.security.DmsUserDetails;
import com.example.dms.services.DocumentService;
import com.example.dms.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
@ContextConfiguration
@WithMockUser(username = "admin", roles = "ADMIN")
class DocumentControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private DocumentService documentService;

	@MockBean
	private UserDetailsService userDetailsService;

	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private DmsUserDetails userDetails;

	DmsUser validUser;
	DmsDocument validDocument;
	DmsType type;
	DmsTypeDTO typeDTO;
	NewDocumentDTO newDocumentDTO;
	DmsDocumentDTO validDocumentDTO;
	ModifyDocumentDTO modifyDocumentDTO;

	private static final String BASE_URL = "/api/v1/documents";
	
	
	public class DmsDetailsArgumentResolver implements HandlerMethodArgumentResolver {
	    @Override
	    public boolean supportsParameter(MethodParameter parameter) {
	        return parameter.getParameterType().isAssignableFrom(DmsUserDetails.class);
	    }

	    @Override
	    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
	                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
	        DmsUser user = DmsUser.builder()
	                .username("admin")
	                .build();
	        DmsUserDetails userDetails = new DmsUserDetails(user, null);
	        return userDetails;
	    }
	}

	@BeforeEach
	void setUp() {
		type = DmsType.builder().typeName("tajni").build();
		typeDTO = DmsTypeDTO.builder().typeName("tajni").build();
		validUser = DmsUser.builder().username("dcrncic").password("12345").firstName("Darjan").lastName("Crnčić")
				.email("darjan.crncic@gmail.com").build();
		validUser.setId(UUID.randomUUID());

		validDocument = DmsDocument.builder().objectName("testni").creator(validUser)
				.description("testni dokument za test").keywords(Arrays.asList(new String[] { "prvi", "drugi" }))
				.immutable(false).type(type).build();

		newDocumentDTO = NewDocumentDTO.builder().objectName("testni").description("testni dokument za test")
				.keywords(Arrays.asList(new String[] { "prvi", "drugi" })).type(type.getTypeName()).username("admin").build();
		validDocumentDTO = DmsDocumentDTO.builder().objectName("testni").description("testni dokument za test")
				.keywords(Arrays.asList(new String[] { "prvi", "drugi" })).type(typeDTO.getTypeName()).build();

		modifyDocumentDTO = ModifyDocumentDTO.builder().objectName("modifiedDoc")
				.description("this is the modified doc").keywords(Arrays.asList(new String[] { "prvi", "drugi" }))
				.type("tajni").build();
	}

	//TODO
//	@Test
//	void createNewDocumentTest() throws Exception {
//		BDDMockito.given(
//				documentService.createDocument(Mockito.any(NewDocumentDTO.class)))
//				.willReturn(validDocumentDTO);
//
//		mockMvc.perform(post(BASE_URL)
//				.content(Utils.stringify(newDocumentDTO)).contentType(MediaType.APPLICATION_JSON).with(user("admin")))
//				.andExpect(status().isCreated()).andExpect(jsonPath("$.object_name", is(validDocument.getObjectName())))
//				.andExpect(jsonPath("$.description", is(validDocument.getDescription())))
//				.andExpect(jsonPath("$.keywords").isArray());
//	}

	@Test
	void deleteDocumentTest() throws Exception {
		doNothing().when(documentService).deleteById(Mockito.any(UUID.class));

		mockMvc.perform(delete(BASE_URL + "/{id}", UUID.randomUUID())).andExpect(status().isOk());
	}

	@Test
	void getAllDocumentsTest() throws Exception {
		BDDMockito.given(documentService.findAll()).willReturn(Arrays.asList(validDocumentDTO));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
	}

	@Test
	void modifyDocumentPutTest() throws Exception {
		BDDMockito.given(documentService.updateDocument(Mockito.any(UUID.class), Mockito.any(ModifyDocumentDTO.class),
				Mockito.anyBoolean())).willReturn(validDocumentDTO);

		mockMvc.perform(put(BASE_URL + "/{id}", UUID.randomUUID()).content(Utils.stringify(modifyDocumentDTO))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	void modifyDocumentPatchTest() throws Exception {
		BDDMockito.given(documentService.updateDocument(Mockito.any(UUID.class), Mockito.any(ModifyDocumentDTO.class),
				Mockito.anyBoolean())).willReturn(validDocumentDTO);

		mockMvc.perform(patch(BASE_URL + "/{id}", UUID.randomUUID()).content(Utils.stringify(modifyDocumentDTO))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

}
