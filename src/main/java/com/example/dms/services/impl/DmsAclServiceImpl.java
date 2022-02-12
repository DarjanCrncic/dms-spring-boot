package com.example.dms.services.impl;

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;

import com.example.dms.domain.DmsDocument;
import com.example.dms.services.DmsAclService;
import com.example.dms.utils.Roles;

@Service
public class DmsAclServiceImpl implements DmsAclService {
	
	JdbcMutableAclService aclService;
	
	public DmsAclServiceImpl(JdbcMutableAclService aclService) {
		super();
		this.aclService = aclService;
	}

	@Override
	public void grantCreatorRightsOnDocument(DmsDocument document, String username) {
		ObjectIdentity oi = new ObjectIdentityImpl(document);
		Sid sid = new PrincipalSid(username);
		Sid adminSid = new GrantedAuthoritySid(Roles.ROLE_ADMIN.name());

		MutableAcl acl = null;
		try {
			acl = (MutableAcl) aclService.readAclById(oi);
		} catch (NotFoundException nfe) {
			acl = aclService.createAcl(oi);
		}
		acl.insertAce(acl.getEntries().size(), BasePermission.ADMINISTRATION, adminSid, true);
		acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, adminSid, true);
		acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, adminSid, true);
		acl.insertAce(acl.getEntries().size(), BasePermission.READ, adminSid, true);

		acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, sid, true);
		acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, sid, true);
		acl.insertAce(acl.getEntries().size(), BasePermission.READ, sid, true);
		aclService.updateAcl(acl);
	}

}
