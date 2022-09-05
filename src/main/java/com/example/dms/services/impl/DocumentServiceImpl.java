package com.example.dms.services.impl;

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
import com.example.dms.security.configuration.acl.CustomBasePermission;
import com.example.dms.services.DmsAclService;
import com.example.dms.services.DocumentService;
import com.example.dms.services.search.SpecificationBuilder;
import com.example.dms.services.search.document.DocumentSpecProvider;
import com.example.dms.utils.Utils;
import com.example.dms.utils.VersionUtils;
import com.example.dms.utils.exceptions.BadRequestException;
import com.example.dms.utils.exceptions.DmsNotFoundException;
import com.example.dms.utils.exceptions.InternalException;
import com.example.dms.utils.exceptions.NotPermitedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentServiceImpl extends EntityCrudServiceImpl<DmsDocument, DmsDocumentDTO> implements DocumentService {

	private final UserRepository userRepository;
	private final DocumentRepository documentRepository;
	private final DocumentMapper documentMapper;
	private final TypeRepository typeRepository;
	private final ContentRepository contentRepository;
	private final FolderRepository folderRepository;

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
	}

	@Override
	@PostFilter("hasPermission(filterObject.id,'com.example.dms.domain.DmsDocument','READ') || hasAuthority('READ_PRIVILEGE')")
	public List<DmsDocumentDTO> findAll() {
		return documentMapper.entityListToDtoList(documentRepository.findAll());
	}

	@Override
	@PreAuthorize("hasAuthority('CREATE_PRIVILEGE')")
	public DmsDocumentDTO createDocument(NewDocumentDTO newDocumentDTO) {
		DmsDocument newDocumentObject = documentMapper.newDocumentDTOToDocument(newDocumentDTO);

		DmsType type = typeRepository.findByTypeName(newDocumentDTO.getType())
				.orElseThrow(() -> new DmsNotFoundException("Given type does not exist."));
		DmsUser creator = userRepository.findByUsername(newDocumentDTO.getUsername())
				.orElseThrow(() -> new DmsNotFoundException("Invalid user."));
		DmsFolder folder = folderRepository.findById(newDocumentDTO.getParentFolderId())
				.orElseThrow(() -> new DmsNotFoundException("Invalid parent folder."));

		if (!creator.isAdminRole() && !folder.getName().equals("/") && !super.aclService.hasRight(folder, newDocumentDTO.getUsername(),
				List.of(BasePermission.CREATE))) {
			throw new NotPermitedException("Insufficient permissions for creating a document in this folder.");
		}

		newDocumentObject.addParentFolder(folder);
		newDocumentObject.addCreator(creator);
		newDocumentObject.addType(type);

		newDocumentObject = documentRepository.save(newDocumentObject);
		newDocumentObject.setRootId(newDocumentObject.getId());
		newDocumentObject.setPredecessorId(newDocumentObject.getId());

		super.aclService.grantRightsOnObject(newDocumentObject, creator.getUsername(), Arrays.asList(
				BasePermission.READ, BasePermission.WRITE, BasePermission.DELETE, BasePermission.ADMINISTRATION,
				CustomBasePermission.VERSION));

		return save(newDocumentObject);
	}

	@Override
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','WRITE') || hasAuthority('WRITE_PRIVILEGE')")
	public DmsDocumentDTO updateDocument(UUID id, ModifyDocumentDTO modifyDocumentDTO, boolean patch) {
		DmsDocument doc = documentRepository.findById(id).orElseThrow(DmsNotFoundException::new);

		if (doc.isImmutable()) {
			throw new BadRequestException("This version of the document is immutable and cannot be modified.");
		}
		if (patch) {
			documentMapper.updateDocumentPatch(modifyDocumentDTO, doc);
		} else {
			documentMapper.updateDocumentPut(modifyDocumentDTO, doc);
		}
		if (modifyDocumentDTO.getType() != null) {
			DmsType newType = typeRepository.findByTypeName(modifyDocumentDTO.getType())
					.orElseThrow(() -> new DmsNotFoundException("Given type does not exist."));
			doc.addType(newType);
		}

		return save(doc);
	}

	@Override
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','WRITE') || hasAuthority('WRITE_PRIVILEGE')")
	public void uploadFile(UUID id, MultipartFile file) {
		DmsDocument doc = documentRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		if (doc.isImmutable()) {
			throw new BadRequestException("Object is immutable and you cannot add content to it.");
		}
		if (doc.getContent() != null) {
			throw new BadRequestException("Object already has content.");
			// TODO create new version?
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
	@PreAuthorize("hasPermission(#document,'READ')")
	public boolean checkIsDocumentValidForDownload(DmsDocument document) {
		if (document.getContent() == null || document.getContent().getContentType() == null
				|| document.getContent().getOriginalFileName() == null || document.getContent().getContentSize() == 0)
			throw new InternalException("Document has corrupted (or no) content, download unavailable.");
		return true;
	}

	@Override
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','VERSION') || hasAuthority('VERSION_PRIVILEGE')")
	public DmsDocumentDTO createNewVersion(UUID id) {
		DmsDocument doc = documentRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		if (doc.isImmutable()) {
			throw new BadRequestException("This version of the document is immutable and cannot be versioned. "
					+ "Currently you can only version the latest version of the document.");
		}

		DmsDocument newVersion = copyDocument(doc);
		newVersion.setRootId(doc.getRootId());
		newVersion.setPredecessorId(doc.getId());
		newVersion.setVersion(VersionUtils.getNextVersion(doc.getVersion()));

		doc.setImmutable(true);
		save(doc);
		newVersion = documentRepository.save(newVersion);
		aclService.copyRightsToAnotherEntity(doc, newVersion);

		return documentMapper.entityToDto(newVersion);
	}

	@Override
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','VERSION') || hasAuthority('VERSION_PRIVILEGE')")
	public DmsDocumentDTO createNewBranch(UUID id) {
		DmsDocument doc = documentRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		if (doc.isBranched()) {
			throw new BadRequestException("This version of the document already has a branch.");
		}

		DmsDocument newVersion = copyDocument(doc);
		newVersion.setRootId(doc.getRootId());
		newVersion.setPredecessorId(doc.getId());
		newVersion.setVersion(doc.getVersion() + ".1");

		doc.setBranched(true);
		save(doc);
		newVersion = documentRepository.save(newVersion);
		aclService.copyRightsToAnotherEntity(doc, newVersion);

		return documentMapper.entityToDto(newVersion);
	}

	@Override
	@PostFilter("hasPermission(filterObject.id,'com.example.dms.domain.DmsDocument','READ') || hasAuthority('READ_PRIVILEGE')")
	public List<DmsDocumentDTO> getAllVersions(UUID id) {
		return documentMapper.entityListToDtoList(documentRepository.findAllByRootId(id));
	}

	private DmsDocument copyDocument(DmsDocument original) {
		return DmsDocument.builder().creator(original.getCreator())
				.description(original.getDescription()).parentFolder(original.getParentFolder())
				.objectName(original.getObjectName()).type(original.getType())
				.keywords(new ArrayList<>(original.getKeywords())).build();
	}

	private DmsContent copyContent(DmsContent original) {
		return DmsContent.builder().content(original.getContent()).contentSize(original.getContentSize())
				.contentType(original.getContentType()).originalFileName(original.getOriginalFileName()).build();
	}

	@Override
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','READ') || hasAuthority('READ_PRIVILEGE')")
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
	@PostFilter("hasPermission(filterObject.id,'com.example.dms.domain.DmsDocument','READ') || hasAuthority('READ_PRIVILEGE')")
	public List<DmsDocumentDTO> searchAll(Optional<String> search, Optional<SortDTO> sort) {
		if (search.isPresent()) {
			SpecificationBuilder<DmsDocument> builder = new SpecificationBuilder<>(new DocumentSpecProvider());
			return documentMapper
					.entityListToDtoList(documentRepository.findAll(builder.parse(search.get()), Utils.toSort(sort)));
		}
		return documentMapper.entityListToDtoList(documentRepository.findAll(Utils.toSort(sort)));
	}

	@Override
	@PreAuthorize("hasPermission(#folderId,'com.example.dms.domain.DmsFolder','CREATE') "
			+ "and @permissionEvaluator.hasPermission(#documentIdList,'com.example.dms.domain.DmsDocument','WRITE',authentication) "
			+ "|| hasAuthority('WRITE_PRIVILEGE') && hasAuthority('CREATE_PRIVILEGE')")
	public List<DmsDocumentDTO> copyDocuments(UUID folderId, List<UUID> documentIdList) {
		DmsFolder folder = folderRepository.findById(folderId).orElseThrow(
				() -> new DmsNotFoundException("Folder with specified id: " + folderId + " could not be found."));
		List<UUID> existingDocs = folder.getDocuments().stream().map(DmsDocument::getId).collect(Collectors.toList());

		List<DmsDocument> documents = documentRepository.findAllById(documentIdList);
		List<DmsDocument> retVal = new ArrayList<>();

		for (DmsDocument doc : documents) {
			DmsDocument copy = copyDocument(doc);
			copy.addParentFolder(folder);

			if (existingDocs.contains(doc.getId())) {
				copy.setObjectName(copy.getObjectName() + " (copy)");
			}
			copy = documentRepository.save(copy);
			copy.setPredecessorId(copy.getId());
			copy.setRootId(copy.getId());
			copy = documentRepository.save(copy);
			aclService.copyRightsToAnotherEntity(doc, copy);

			DmsContent copyContent = null;
			if (doc.getContent() != null) {
				copyContent = copyContent(doc.getContent());
				copyContent.setDocument(copy);
				copyContent = contentRepository.save(copyContent);
				copy.setContent(copyContent);
			}
			retVal.add(copy);
		}

		return mapper.entityListToDtoList(retVal);
	}

	@Override
	@PreAuthorize("hasPermission(#folderId,'com.example.dms.domain.DmsFolder','CREATE') "
			+ "and @permissionEvaluator.hasPermission(#documentIdList,'com.example.dms.domain.DmsDocument','WRITE',authentication) "
			+ "|| hasAuthority('WRITE_PRIVILEGE') && hasAuthority('CREATE_PRIVILEGE')")
	public List<DmsDocumentDTO> cutDocuments(UUID folderId, List<UUID> documentIdList) {
		DmsFolder folder = folderRepository.findById(folderId).orElseThrow(
				() -> new DmsNotFoundException("Folder with specified id: " + folderId + " could not be found."));

		List<DmsDocument> documents = documentRepository.findAllById(documentIdList);
		List<DmsDocument> retVal = new ArrayList<>();

		for (DmsDocument doc : documents) {
			doc.setParentFolder(folder);
			retVal.add(documentRepository.save(doc));
		}

		return mapper.entityListToDtoList(retVal);
	}

	@Override
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsDocument','DELETE') "
			+ "or hasAuthority('DELETE_PRIVILEGE')")
	public void deleteById(UUID id) {
		DmsDocument toDelete = documentRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		if (toDelete.isImmutable()) {
			throw new BadRequestException("Document cannot be deleted since it is immutable.");
		}
		if (toDelete.isBranched()) {
			throw new BadRequestException("Document cannot be deleted since child branches still exist.");
		}
		if (toDelete.getPredecessorId() != null) {
			DmsDocument prevVersion = documentRepository.findById(toDelete.getPredecessorId()).orElse(null);
			if (prevVersion != null) {
				if (prevVersion.isBranched() && toDelete.getVersion().startsWith(prevVersion.getVersion())) {
					prevVersion.setBranched(false);
				}
				prevVersion.setImmutable(false);
				documentRepository.save(prevVersion);
			}
		}
		super.deleteById(id);
	}
}
