package com.example.dms.services.impl;

import com.example.dms.api.dtos.folder.DmsFolderDTO;
import com.example.dms.api.dtos.folder.FolderTreeDTO;
import com.example.dms.api.mappers.FolderMapper;
import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsFolder;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.services.*;
import com.example.dms.utils.ActionEnum;
import com.example.dms.utils.StringUtils;
import com.example.dms.utils.exceptions.BadRequestException;
import com.example.dms.utils.exceptions.DmsNotFoundException;
import com.example.dms.utils.exceptions.NotPermitedException;
import com.example.dms.utils.exceptions.UniqueConstraintViolatedException;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class FolderServiceImpl extends EntityCrudServiceImpl<DmsFolder, DmsFolderDTO> implements FolderService {

	private final FolderRepository folderRepository;
	private final FolderMapper folderMapper;
	private final DocumentRepository documentRepository;
	private final DocumentService documentService;
	private final NotificationService notificationService;
	private final AuthenticationUtil authUtil;

	public FolderServiceImpl(FolderRepository folderRepository, FolderMapper folderMapper,
			DocumentRepository documentRepository, DmsAclService aclService, DocumentService documentService,
			NotificationService notificationService, AuthenticationUtil authUtil) {
		super(folderRepository, folderMapper, aclService);
		this.folderRepository = folderRepository;
		this.folderMapper = folderMapper;
		this.documentRepository = documentRepository;
		this.documentService = documentService;
		this.notificationService = notificationService;
		this.authUtil = authUtil;
	}

	@Override
	@PostFilter("hasPermission(filterObject.id,'com.example.dms.domain.DmsFolder','READ') || hasAuthority('READ_PRIVILEGE') || filterObject.name == '/'")
	public List<DmsFolderDTO> findAll() {
		return folderMapper.entityListToDtoList(folderRepository.findAll());
	}

	@Override
	@PostFilter("hasPermission(filterObject.id,'com.example.dms.domain.DmsFolder','READ') || hasAuthority('READ_PRIVILEGE') || filterObject.name == '/'")
	public List<FolderTreeDTO> getFolderTreeNew() {
		return folderMapper.dmsFolderListToFolderTreeList(folderRepository.findAll());
	}

	@Override
	@PreAuthorize("hasAuthority('CREATE_PRIVILEGE')")
	public DmsFolderDTO createFolder(String name, Integer parentFolderId) {
		DmsFolder parentFolder = folderRepository.findById(parentFolderId)
				.orElseThrow(DmsNotFoundException::new);
		checkConstraints(name, parentFolderId);
		
		if (!parentFolder.getName().equals("/") && !super.aclService.hasRight(parentFolder, authUtil.getUserName(), List.of(BasePermission.CREATE))) {
			throw new NotPermitedException("User: " + authUtil.getUserName()
					+ " has insufficient permissions for creating a folder at this path.");
		}

		DmsFolder newFolder = DmsFolder.builder().name(name).build();
		newFolder.addParentFolder(parentFolder);
		newFolder = folderRepository.save(newFolder);

		super.aclService.grantRightsOnObject(newFolder, authUtil.getUserName(), Arrays.asList(BasePermission.READ,
				BasePermission.WRITE, BasePermission.CREATE, BasePermission.DELETE, BasePermission.ADMINISTRATION));
		this.notificationService.createAclNotification(newFolder, ActionEnum.CREATE);
		return folderMapper.entityToDto(newFolder);
	}

	@Override
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsFolder','WRITE') || hasAuthority('WRITE_PRIVILEGE')")
	public DmsFolderDTO updateFolder(Integer id, String newName) {
		DmsFolder oldFolder = folderRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		checkConstraints(newName, oldFolder.getParentFolder().getId());
		
		oldFolder.setName(newName);
		this.notificationService.createAclNotification(oldFolder, ActionEnum.UPDATE);
		return save(oldFolder);
	}

	private void checkConstraints(String name, Integer parentFolderId) {
		if (!StringUtils.validateFolderName(name)) {
			throw new BadRequestException("Folder name: '" + name + "' does not match required parameters.");
		}
		if (folderRepository.findByNameAndParentFolderId(name, parentFolderId).isPresent()) {
			throw new UniqueConstraintViolatedException("Folder with name: '" + name + "' already exists.");
		}
	}
	
	@Override
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsFolder','DELETE') || hasAuthority('DELETE_PRIVILEGE')")
	public void deleteFolder(Integer id) {
		DmsFolder folder = folderRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		folder.getSubfolders().forEach(sub -> deleteFolder(sub.getId()));
		folder.getDocuments().forEach(doc -> documentService.deleteById(doc.getId()));
		this.notificationService.createAclNotification(folder, ActionEnum.DELETE);
		this.deleteById(id);
	}

	// TODO: still not used, needs a check
	@Override
	@PreAuthorize("hasPermission(#folderId,'com.example.dms.domain.DmsFolder','WRITE') "
			+ "and @permissionEvaluator.hasPermission(#documentIdList,'com.example.dms.domain.DmsDocument','WRITE',authentication) "
			+ "|| hasAuthority('WRITE_PRIVILEGE')")
	public DmsFolderDTO moveFilesToFolder(Integer folderId, List<Integer> documentIdList) {
		DmsFolder folder = folderRepository.findById(folderId).orElseThrow(
				() -> new DmsNotFoundException("Folder with specified id: " + folderId + " could not be found."));
		for (Integer documentId : documentIdList) {
			DmsDocument doc = documentRepository.findById(documentId)
					.orElseThrow(() -> new BadRequestException("Ivalid document id: " + documentId + "."));
			folder.addDocument(doc);
			folder = folderRepository.save(folder);
		}
		return folderMapper.entityToDto(folder);
	}

	

	// TODO: move subfolder to other folder
	// TODO: copy folder to another folder
}
