package com.example.dms.services.impl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.api.mappers.DocumentMapper;
import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsType;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.repositories.TypeRepository;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.DocumentService;
import com.example.dms.utils.exceptions.BadRequestException;
import com.example.dms.utils.exceptions.InternalException;
import com.example.dms.utils.exceptions.NotFoundException;

@Service
@Transactional
public class DocumentServiceImpl extends EntityCrudServiceImpl<DmsDocument, DmsDocumentDTO> implements DocumentService {

	UserRepository userRepository;
	DocumentRepository documentRepository;
	DocumentMapper documentMapper;
	TypeRepository typeRepository;

	public DocumentServiceImpl(UserRepository userRepository, DocumentRepository documentRepository,
			DocumentMapper documentMapper, TypeRepository typeRepository) {
		super(documentRepository, documentMapper);
		this.userRepository = userRepository;
		this.documentRepository = documentRepository;
		this.documentMapper = documentMapper;
		this.typeRepository = typeRepository;
	}

	@Override
	public DmsDocumentDTO createNewDocument(NewDocumentDTO newDocumentDTO) {
		// TODO: Remove hardcoded user.
		DmsDocument newDocumentObject = documentMapper.newDocumentDTOToDocument(newDocumentDTO);
		
		DmsType type = typeRepository.findByTypeName(newDocumentDTO.getTypeName()).orElse(null);
		DmsUser creator = userRepository.findByUsername("user").orElseThrow(() -> new NotFoundException("Invalid creator user."));
		
		persistDocumentToUser(creator, newDocumentObject);
		persistDocumentToType(type, newDocumentObject);
		newDocumentObject.setRootId(newDocumentObject.getId());
		newDocumentObject.setPredecessorId(newDocumentObject.getId());
		return save(newDocumentObject);
	}
	
	private void persistDocumentToType(DmsType type, DmsDocument document) {
		if (type != null && !type.getDocuments().contains(document)) {
			document.setType(type);
			document = documentRepository.save(document);
			type.getDocuments().add(document);
			type = typeRepository.save(type);
		}
	}
	
	private void persistDocumentToUser(DmsUser user, DmsDocument document) {
		if (!user.getDocuments().contains(document)) {
			document.setCreator(user);
			document = documentRepository.save(document);
			user.getDocuments().add(document);
			user = userRepository.save(user);
		}
	}

	@Override
	public DmsDocumentDTO updateDocument(UUID id, ModifyDocumentDTO modifyDocumentDTO, boolean patch) {
		DmsDocument doc = documentRepository.findById(id).orElseThrow(NotFoundException::new);
		if (doc.isImutable()) {
			throw new BadRequestException("This version of the document is immutable and cannot be modified.");
		}
		if (patch) {
			documentMapper.updateDocumentPatch(modifyDocumentDTO, doc);
		} else {
			documentMapper.updateDocumentPut(modifyDocumentDTO, doc);
		}
		return save(doc);
	}

	@Override
	public void uploadFile(UUID id, MultipartFile file) {
		DmsDocument doc = documentRepository.findById(id).orElseThrow(NotFoundException::new);
		if (doc.isImutable()) {
			throw new BadRequestException("Object is immutable and you cannot add content to it.");
		}
		String path = file.getOriginalFilename();
		if (path == null) {
			throw new BadRequestException("Original filename is null.");
		}

		String originalFileName = StringUtils.cleanPath(path);
		try {
			doc.setContent(file.getBytes());
		} catch (IOException e) {
			throw new InternalException(
					"Could not upload file: '" + originalFileName + "' for document: '" + id + "'.");
		}
		doc.setContentSize(file.getSize());
		doc.setContentType(file.getContentType());
		doc.setOriginalFileName(originalFileName);
		save(doc);
	}

	@Override
	public boolean checkIsDocumentValidForDownload(DmsDocument document) {
		if (document.getContent() == null || document.getContentType() == null
				|| document.getOriginalFileName() == null)
			throw new InternalException("Document has corrupted (or no) content, download unavailable.");
		return true;
	}

	@Override
	public DmsDocumentDTO createNewVersion(UUID id) {
		DmsDocument doc = documentRepository.findById(id).orElseThrow(NotFoundException::new);
		if (doc.isImutable()) {
			throw new BadRequestException("This version of the document is immutable and cannot be versioned. "
					+ "Currently you can only version the latest version of the document.");
		}

		DmsDocument newVersion = copyDocument(doc);
		newVersion.setRootId(doc.getRootId());
		newVersion.setPredecessorId(doc.getId());
		newVersion.setVersion(doc.getVersion() + 1);

		doc.setImutable(true);
		save(doc);

		return save(newVersion);
	}

	@Override
	public List<DmsDocumentDTO> getAllVersions(UUID id) {
		return documentMapper.entityListToDtoList(documentRepository.getAllVersions(id));
	}

	private DmsDocument copyDocument(DmsDocument original) {
		return DmsDocument.builder().content(original.getContent()).contentSize(original.getContentSize())
				.contentType(original.getContentType()).creator(original.getCreator())
				.description(original.getDescription()).parentFolder(original.getParentFolder())
				.objectName(original.getObjectName()).originalFileName(original.getOriginalFileName()).build();
	}

	@Override
	public List<DmsDocumentDTO> getAllDocuments() {
		return documentMapper.entityListToDtoList(documentRepository.findAll());
	}

	@Override
	public ResponseEntity<byte[]> downloadContent(UUID id) {
		DmsDocument document = documentRepository.findById(id).orElseThrow(NotFoundException::new);
		checkIsDocumentValidForDownload(document);
		return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getOriginalFileName() + "\"")
                .contentType(MediaType.valueOf(document.getContentType()))
                .body(document.getContent());
	}
}
