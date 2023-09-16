package com.example.dms.services;

import com.example.dms.api.dtos.SortDTO;
import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.api.dtos.document.ModifyDocumentDTO;
import com.example.dms.api.dtos.document.NewDocumentDTO;
import com.example.dms.domain.DmsDocument;

import java.util.List;

public interface DocumentService extends CrudService<DmsDocument, DmsDocumentDTO, Integer>{

	DmsDocumentDTO createDocument(NewDocumentDTO newDocumentDTO);

	DmsDocumentDTO createNewVersion(Integer id);

	DmsDocumentDTO createNewBranch(Integer id);

	List<DmsDocumentDTO> getAllVersions(Integer id);

	DmsDocumentDTO updateDocument(Integer id, ModifyDocumentDTO modifyDocumentDTO, boolean patch);

	List<DmsDocumentDTO> searchAll(String search, SortDTO sort);

	List<DmsDocumentDTO> copyDocuments(Integer folderId, List<Integer> documentIdList);

	List<DmsDocumentDTO> cutDocuments(Integer folderId, List<Integer> documents);
}
