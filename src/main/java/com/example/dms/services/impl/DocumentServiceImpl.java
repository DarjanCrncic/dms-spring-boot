package com.example.dms.services.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.dms.api.dtos.SortDTO;
import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.api.mappers.DocumentMapper;
import com.example.dms.domain.DmsContent;
import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsFolder;
import com.example.dms.domain.DmsType;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.ContentRepository;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.repositories.TypeRepository;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.DmsAclService;
import com.example.dms.services.DocumentService;
import com.example.dms.services.search.SpecificationBuilder;
import com.example.dms.services.search.document.DocumentSpecProvider;
import com.example.dms.utils.Utils;
import com.example.dms.utils.exceptions.BadRequestException;
import com.example.dms.utils.exceptions.DmsNotFoundException;
import com.example.dms.utils.exceptions.InternalException;

@Service
@Transactional
public class DocumentServiceImpl extends EntityCrudServiceImpl<DmsDocument, DmsDocumentDTO> implements DocumentService {

	UserRepository userRepository;
	DocumentRepository documentRepository;
	DocumentMapper documentMapper;
	TypeRepository typeRepository;
	ContentRepository contentRepository;
	FolderRepository folderRepository;
	DmsAclService aclService;

	public DocumentServiceImpl(UserRepository userRepository, DocumentRepository documentRepository,
			DocumentMapper documentMapper, TypeRepository typeRepository, DmsAclService aclService,
			ContentRepository contentRepository, FolderRepository folderRepository) {
		super(documentRepository, documentMapper, aclService);
		this.userRepository = userRepository;
		this.documentRepository = documentRepository;
		this.documentMapper = documentMapper;
		this.typeRepository = typeRepository;
		this.contentRepository = contentRepository;
		this.folderRepository = folderRepository;
		this.aclService = aclService;
	}

	@Override
	@PostFilter("hasPermission(filterObject,'READ') && hasAuthority('READ_PRIVILEGE')")
	public List<DmsDocumentDTO> findAll() {
		return documentMapper.entityListToDtoList(documentRepository.findAll());
	}

	@Override
	@PreAuthorize("hasAuthority('CREATE_PRIVILEGE') || hasRole('ADMIN')")
	public DmsDocumentDTO createDocument(NewDocumentDTO newDocumentDTO) {
		DmsDocument newDocumentObject = documentMapper.newDocumentDTOToDocument(newDocumentDTO);
		
		// TODO: REplace with throws
		DmsType type = typeRepository.findByTypeName(newDocumentDTO.getType()).orElse(null);
		DmsUser creator = userRepository.findByUsername(newDocumentDTO.getUsername()).orElseThrow(() -> new DmsNotFoundException("Invalid user."));
		DmsFolder folder = folderRepository.findByPath("/").orElseThrow(() -> new InternalException("Root folder not set up."));
		
		String path = newDocumentDTO.getParentFolder();
		if(path != null && !path.isEmpty()) {
			folder = folderRepository.findByPath(path).orElseThrow(() -> new DmsNotFoundException("Invalid parent folder."));
		}
		newDocumentObject.addParentFolder(folder);
		newDocumentObject.addCreator(creator);
		newDocumentObject.addType(type);
		
		newDocumentObject = documentRepository.save(newDocumentObject);
		newDocumentObject.setRootId(newDocumentObject.getId());
		newDocumentObject.setPredecessorId(newDocumentObject.getId());
		
		super.aclService.grantCreatorRights(newDocumentObject, creator.getUsername());
		
		return save(newDocumentObject);
	}

	@Override
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','WRITE') && hasAuthority('WRITE_PRIVILEGE')")
	public DmsDocumentDTO updateDocument(UUID id, ModifyDocumentDTO modifyDocumentDTO, boolean patch) {
		DmsDocument doc = documentRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		// TODO: replace with throws
		DmsType type = typeRepository.findByTypeName(modifyDocumentDTO.getType()).orElse(null);
		if (doc.isImutable()) {
			throw new BadRequestException("This version of the document is immutable and cannot be modified.");
		}
		if (patch) {
			documentMapper.updateDocumentPatch(modifyDocumentDTO, doc);
		} else {
			documentMapper.updateDocumentPut(modifyDocumentDTO, doc);
			if (type == null) {
				doc.setType(null);
			}
		}
		if (type != null) {
			doc.addType(type);
			doc = documentRepository.save(doc);
		}

		return save(doc);
	}

	@Override
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','WRITE') && hasAuthority('WRITE_PRIVILEGE')")
	public void uploadFile(UUID id, MultipartFile file) {
		DmsDocument doc = documentRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		if (doc.isImutable()) {
			throw new BadRequestException("Object is immutable and you cannot add content to it.");
		}
		String path = file.getOriginalFilename();
		if (path == null) {
			throw new BadRequestException("Original filename is null.");
		}

		String originalFileName = StringUtils.cleanPath(path);
		DmsContent content = null;
		try {
			content = DmsContent.builder().content(file.getBytes()).contentSize(file.getSize())
					.contentType(file.getContentType()).originalFileName(file.getOriginalFilename()).document(doc)
					.build();
			contentRepository.save(content);
			doc.setContent(content);
		} catch (IOException e) {
			throw new InternalException(
					"Could not upload file: '" + originalFileName + "' for document: '" + id + "'.");
		}
		save(doc);
	}

	@Override
	@PreAuthorize("hasAuthority('READ_PRIVILEGE')")
	public boolean checkIsDocumentValidForDownload(DmsDocument document) {
		if (document.getContent() == null || document.getContent().getContentType() == null
				|| document.getContent().getOriginalFileName() == null || document.getContent().getContentSize() == 0)
			throw new InternalException("Document has corrupted (or no) content, download unavailable.");
		return true;
	}

	@Override
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','WRITE') AND hasAuthority('VERSION_PRIVILEGE')")
	public DmsDocumentDTO createNewVersion(UUID id) {
		DmsDocument doc = documentRepository.findById(id).orElseThrow(DmsNotFoundException::new);
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
	@PostFilter("hasPermission(filterObject,'READ') && hasAuthority('READ_PRIVILEGE')")
	public List<DmsDocumentDTO> getAllVersions(UUID id) {
		return documentMapper.entityListToDtoList(documentRepository.getAllVersions(id));
	}

	private DmsDocument copyDocument(DmsDocument original) {
		return DmsDocument.builder().content(original.getContent()).creator(original.getCreator())
				.description(original.getDescription()).parentFolder(original.getParentFolder())
				.objectName(original.getObjectName()).build();
	}

	@Override
	@PostFilter("hasPermission(filterObject,'READ') && hasAuthority('READ_PRIVILEGE')")
	public List<DmsDocumentDTO> getAllDocuments(Optional<SortDTO> sort) {
		return documentMapper.entityListToDtoList(documentRepository.findAll(Utils.toSort(sort)));
	}

	@Override
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','READ') && hasAuthority('READ_PRIVILEGE')")
	public ResponseEntity<byte[]> downloadContent(UUID id) {
		DmsDocument document = documentRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		checkIsDocumentValidForDownload(document);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + document.getContent().getOriginalFileName() + "\"")
				.contentType(MediaType.valueOf(document.getContent().getContentType()))
				.header("File-Name", document.getContent().getOriginalFileName())
				.body(document.getContent().getContent());
	}

	@Override
	@PostFilter("hasPermission(filterObject,'READ') && hasAuthority('READ_PRIVILEGE')")
	public List<DmsDocumentDTO> searchAll(String search, Optional<SortDTO> sort) {
		SpecificationBuilder<DmsDocument> builder = new SpecificationBuilder<>(new DocumentSpecProvider());
		return documentMapper.entityListToDtoList(documentRepository.findAll(builder.parse(search), Utils.toSort(sort)));
	}

	@Override
	public void deleteInBatch(List<UUID> ids) {
		ids.stream().forEach(id -> {
			deleteById(id);
		});
	}
}
