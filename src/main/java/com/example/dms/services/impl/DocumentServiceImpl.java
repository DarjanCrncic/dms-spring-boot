package com.example.dms.services.impl;

import com.example.dms.api.dtos.SortDTO;
import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.api.mappers.DocumentMapper;
import com.example.dms.domain.*;
import com.example.dms.repositories.*;
import com.example.dms.security.configuration.acl.CustomBasePermission;
import com.example.dms.services.AuthenticationUtil;
import com.example.dms.services.DmsAclService;
import com.example.dms.services.DocumentService;
import com.example.dms.services.NotificationService;
import com.example.dms.services.search.SpecificationBuilder;
import com.example.dms.services.search.document.DocumentSpecProvider;
import com.example.dms.utils.ActionEnum;
import com.example.dms.utils.Utils;
import com.example.dms.utils.VersionUtils;
import com.example.dms.utils.exceptions.BadRequestException;
import com.example.dms.utils.exceptions.DmsNotFoundException;
import com.example.dms.utils.exceptions.NotPermitedException;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	private final NotificationService notificationService;
	private final AuthenticationUtil authUtil;

	public DocumentServiceImpl(UserRepository userRepository, DocumentRepository documentRepository,
							   DocumentMapper documentMapper, TypeRepository typeRepository, DmsAclService aclService,
							   ContentRepository contentRepository, FolderRepository folderRepository,
							   NotificationService notificationService, AuthenticationUtil authUtil) {
		super(documentRepository, documentMapper, aclService);
		this.userRepository = userRepository;
		this.documentRepository = documentRepository;
		this.documentMapper = documentMapper;
		this.typeRepository = typeRepository;
		this.contentRepository = contentRepository;
		this.folderRepository = folderRepository;
		this.notificationService = notificationService;
		this.authUtil = authUtil;
	}

	@Override
	@PostFilter("hasAuthority('READ_PRIVILEGE') || hasPermission(filterObject.id,'com.example.dms.domain.DmsDocument','READ')")
	public List<DmsDocumentDTO> findAll() {
		return documentMapper.entityListToDtoList(documentRepository.findAll());
	}

	@Override
	@PreAuthorize("hasAuthority('CREATE_PRIVILEGE') || #newDocumentDTO.rootFolder == true || " +
			"hasPermission(#newDocumentDTO.parentFolderId,'com.example.dms.domain.DmsFolder','CREATE')")
	public DmsDocumentDTO createDocument(NewDocumentDTO newDocumentDTO) {
		DmsDocument newDocumentObject = documentMapper.newDocumentDTOToDocument(newDocumentDTO);

		DmsType type = typeRepository.findByTypeName(newDocumentDTO.getType())
				.orElseThrow(() -> new DmsNotFoundException("Given type does not exist."));
		DmsUser creator = userRepository.findByUsername(authUtil.getUserName())
				.orElseThrow(() -> new DmsNotFoundException("Invalid user."));
		DmsFolder folder = folderRepository.findById(newDocumentDTO.getParentFolderId())
				.orElseThrow(() -> new DmsNotFoundException("Invalid parent folder."));

		if (!folder.getName().equals("/") && !super.aclService.hasRight(folder, authUtil.getUserName(), List.of(BasePermission.CREATE))) {
			throw new NotPermitedException("Insufficient permissions for creating a document in this folder.");
		}

		newDocumentObject.addParentFolder(folder);
		newDocumentObject.addCreator(creator);
		newDocumentObject.addType(type);

		newDocumentObject = documentRepository.save(newDocumentObject);
		newDocumentObject.setRootId(newDocumentObject.getId());
		newDocumentObject.setPredecessorId(newDocumentObject.getId());

		grantCreatorRights(newDocumentObject, creator.getUsername());

		notificationService.createAclNotification(newDocumentObject, ActionEnum.CREATE);
		return save(newDocumentObject);
	}

	private void grantCreatorRights(DmsDocument newDocumentObject, String username) {
		super.aclService.grantRightsOnObject(newDocumentObject, username, Arrays.asList(
				BasePermission.READ,
				BasePermission.WRITE,
				BasePermission.DELETE,
				BasePermission.CREATE,
				BasePermission.ADMINISTRATION,
				CustomBasePermission.VERSION));
	}

	@Override
	@PreAuthorize("hasAuthority('WRITE_PRIVILEGE') || hasPermission(#id,'com.example.dms.domain.DmsDocument','WRITE')")
	public DmsDocumentDTO updateDocument(Integer id, ModifyDocumentDTO modifyDocumentDTO, boolean patch) {
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
		notificationService.createAclNotification(doc, ActionEnum.UPDATE);
		return save(doc);
	}

	@Override
	@PreAuthorize("hasAuthority('VERSION_PRIVILEGE') || hasPermission(#id,'com.example.dms.domain.DmsDocument','VERSION')")
	public DmsDocumentDTO createNewVersion(Integer id) {
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
	@PreAuthorize("hasAuthority('VERSION_PRIVILEGE') || hasPermission(#id,'com.example.dms.domain.DmsDocument','VERSION')")
	public DmsDocumentDTO createNewBranch(Integer id) {
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
	@PostFilter("hasAuthority('READ_PRIVILEGE') || hasPermission(filterObject.id,'com.example.dms.domain.DmsDocument','READ')")
	public List<DmsDocumentDTO> getAllVersions(Integer id) {
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
	@PostFilter("hasAuthority('READ_PRIVILEGE') || hasPermission(filterObject.id,'com.example.dms.domain.DmsDocument','READ')")
	public List<DmsDocumentDTO> searchAll(String search, SortDTO sort) {
		if (search != null) {
			SpecificationBuilder<DmsDocument> builder = new SpecificationBuilder<>(new DocumentSpecProvider());
			return documentMapper
					.entityListToDtoList(documentRepository.findAll(builder.parse(search), Utils.toSort(sort)));
		}
		return documentMapper.entityListToDtoList(documentRepository.findAll(Utils.toSort(sort)));
	}

	@Override
	@PreAuthorize("hasPermission(#folderId,'com.example.dms.domain.DmsFolder','CREATE') "
			+ "&& @permissionEvaluator.hasPermission(#documentIdList,'com.example.dms.domain.DmsDocument','CREATE',authentication) "
			+ "|| hasAuthority('CREATE_PRIVILEGE')")
	public List<DmsDocumentDTO> copyDocuments(Integer folderId, List<Integer> documentIdList) {
		DmsFolder folder = folderRepository.findById(folderId).orElseThrow(
				() -> new DmsNotFoundException("Folder with specified id: " + folderId + " could not be found."));
		List<Integer> existingDocs = folder.getDocuments().stream().map(DmsDocument::getId).collect(Collectors.toList());

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
			grantCreatorRights(copy, authUtil.getUserName());

			if (doc.getContent() != null) {
				DmsContent copyContent = copyContent(doc.getContent());
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
			+ "&& @permissionEvaluator.hasPermission(#documentIdList,'com.example.dms.domain.DmsDocument','CREATE',authentication) "
			+ "|| hasAuthority('CREATE_PRIVILEGE')")
	public List<DmsDocumentDTO> cutDocuments(Integer folderId, List<Integer> documentIdList) {
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
	@PreAuthorize("hasAuthority('DELETE_PRIVILEGE') || hasPermission(#id,'com.example.dms.domain.DmsDocument','DELETE')")
	public void deleteById(Integer id) {
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
		notificationService.createAclNotification(toDelete, ActionEnum.DELETE);
		super.deleteById(id);
	}
}
