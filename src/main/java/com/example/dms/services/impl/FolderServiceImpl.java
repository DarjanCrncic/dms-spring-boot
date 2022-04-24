package com.example.dms.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dms.api.dtos.folder.DmsFolderDTO;
import com.example.dms.api.dtos.folder.FolderTreeDTO;
import com.example.dms.api.mappers.FolderMapper;
import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsFolder;
import com.example.dms.repositories.DocumentRepository;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.services.DmsAclService;
import com.example.dms.services.FolderService;
import com.example.dms.utils.Constants;
import com.example.dms.utils.exceptions.BadRequestException;
import com.example.dms.utils.exceptions.DmsNotFoundException;
import com.example.dms.utils.exceptions.UniqueConstraintViolatedException;

@Service
@Transactional
public class FolderServiceImpl extends EntityCrudServiceImpl<DmsFolder, DmsFolderDTO> implements FolderService {

	FolderRepository folderRepository;
	FolderMapper folderMapper;
	DocumentRepository documentRepository;

	public FolderServiceImpl(FolderRepository folderRepository, FolderMapper folderMapper,
			DocumentRepository documentRepository, DmsAclService aclService) {
		super(folderRepository, folderMapper, aclService);
		this.folderRepository = folderRepository;
		this.folderMapper = folderMapper;
		this.documentRepository = documentRepository;
	}
	
	@Override
	@PostFilter("hasPermission(filterObject,'READ') && hasAuthority('READ_PRIVILEGE')")
	public List<DmsFolderDTO> findAll() {
		return folderMapper.entityListToDtoList(folderRepository.findAll());
	}
	
	@Override
	@PreAuthorize("hasAuthority('READ_PRIVILEGE')")
	public FolderTreeDTO getSubfolderTree(String path) {
		DmsFolder parentFolder = folderRepository.findByPath(getParentFolderPath(path)).orElseThrow(DmsNotFoundException::new);;
		return folderMapper.dmsFolderToFolderTree(parentFolder);
	}

	@Override
	public DmsFolderDTO findByPath(String path) {
		Optional<DmsFolder> folder = folderRepository.findByPath(path);
		if (folder.isEmpty()) {
			throw new DmsNotFoundException("Folder with specified path: '" + path + "' was not found.");
		}
		return folderMapper.entityToDto(folder.get());
	}

	@Override
	@PreAuthorize("hasAuthority('CREATE_PRIVILEGE')")
	public DmsFolderDTO createFolder(String path) {
		checkPath(path);
		DmsFolder parentFolder = folderRepository.findByPath(getParentFolderPath(path))
				.orElseThrow(DmsNotFoundException::new);
		DmsFolder newFolder = DmsFolder.builder().path(path).build();

		newFolder.addParentFolder(parentFolder);
		newFolder = folderRepository.save(newFolder);
		
		super.aclService.grantCreatorRights(newFolder, "creator"); // TODO: change to principal
		return folderMapper.entityToDto(newFolder);
	}

	@Override
	@PreAuthorize("hasPermission(#id,'com.example.dms.domain.DmsFolder','WRITE')")
	public DmsFolderDTO updateFolder(UUID id, String path) {
		// TODO: when updating folder it could cause the folder structure to change, 
		// add check to make sure only the folder name is changed
		checkPath(path);
		DmsFolder oldFolder = folderRepository.findById(id).orElseThrow(DmsNotFoundException::new);
		oldFolder.setPath(path);
		return save(oldFolder);
	}

	private void checkPath(String path) {
		if (!validateFolderPath(path)) {
			throw new BadRequestException("Folder path: '" + path + "' does not match required parameters.");
		}
		if (folderRepository.findByPath(path).isPresent()) {
			throw new UniqueConstraintViolatedException("Folder with path: '" + path + "' already exists.");
		}
	}

	public static boolean validateFolderPath(String path) {
		Pattern p = Pattern.compile(Constants.FOLDER_PATH_REGEX);
		Matcher m = p.matcher(path);
		return m.matches();
	}

	public static String getParentFolderPath(String path) {
		int i = path.lastIndexOf("/");
		return i == 0 ? "/" : path.substring(0, i);
	}

	@Override
	@PreAuthorize("hasPermission(#folderId,'com.example.dms.domain.DmsFolder','WRITE') "
			+ "and @permissionEvaluator.hasPermission(#documentIdList,'com.example.dms.domain.DmsDocument','WRITE',authentication)")
	public DmsFolderDTO moveFilesToFolder(UUID folderId, List<UUID> documentIdList) {
		DmsFolder folder = folderRepository.findById(folderId)
				.orElseThrow(() -> new DmsNotFoundException("Folder with specified id: " + folderId + " could not be found."));
		for (UUID documentId : documentIdList) {
			DmsDocument doc = documentRepository.findById(documentId)
					.orElseThrow(() -> new BadRequestException("Ivalid document id: " + documentId + "."));
			folder.addDocument(doc);
			folder = folderRepository.save(folder);
		}
		return folderMapper.entityToDto(folder);
	}
	
	// TODO: no checking permissions for documents when deleting folder
	// TODO: copy file to folder 
	// TODO: move subfolder to other folder
	// TODO: copy folder to another folder
}
