package com.example.dms.services;

import java.util.List;
import java.util.UUID;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import com.example.dms.domain.DmsDocument;

public interface DmsAclService {

	void grantCreatorRightsOnDocument(DmsDocument document, String username);

	void grantRightsOnDocument(UUID documentId, Sid sid, List<Permission> permissions);

	void revokeRightsOnDocument(UUID documentId, Sid sid, List<Permission> permissions);

}
