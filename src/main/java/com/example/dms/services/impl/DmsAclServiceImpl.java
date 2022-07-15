package com.example.dms.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;

import com.example.dms.api.dtos.administration.GrantDTO;
import com.example.dms.domain.security.AclAllowedClass;
import com.example.dms.services.DmsAclService;
import com.example.dms.utils.Permissions;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DmsAclServiceImpl implements DmsAclService {

	JdbcMutableAclService aclService;

	public DmsAclServiceImpl(JdbcMutableAclService aclService) {
		super();
		this.aclService = aclService;
	}

	@Override
	public <T extends AclAllowedClass> void grantRightsOnObject(T object, String username,
			Collection<Permission> permissions) {
		Sid sid = new PrincipalSid(username);
		this.grantRightsOnObject(object, sid, permissions);
	}

	@Override
	public <T extends AclAllowedClass> void grantRightsOnObject(T object, Sid sid, Collection<Permission> permissions) {
		ObjectIdentity oi = new ObjectIdentityImpl(object);
		MutableAcl acl = null;
		try {
			acl = (MutableAcl) aclService.readAclById(oi);
		} catch (NotFoundException nfe) {
			acl = aclService.createAcl(oi);
		}
		log.debug("granting '{}' rights on object (" + object.getClass() + "): {}", ((PrincipalSid) sid).getPrincipal(),
				Permissions.getByMasks(permissions.stream().map(perm -> perm.getMask()).collect(Collectors.toList())));

		for (Permission permission : permissions) {
			acl.insertAce(acl.getEntries().size(), permission, sid, true);
		}
		aclService.updateAcl(acl);
	}

	@Override
	public <T extends AclAllowedClass> void revokeRightsOnObject(T object, Sid sid, List<Permission> permissions) {
		ObjectIdentity oi = new ObjectIdentityImpl(object);

		MutableAcl acl = null;
		try {
			acl = (MutableAcl) aclService.readAclById(oi);
		} catch (NotFoundException nfe) {
			log.error(nfe.getMessage(), nfe);
			throw nfe;
		}

		boolean removeAll = permissions == null || permissions.isEmpty();

		for (int i = acl.getEntries().size() - 1; i >= 0; i--) {
			AccessControlEntry entry = acl.getEntries().get(i);
			if (entry.getSid().equals(sid) && (removeAll || permissions.contains(entry.getPermission()))) {
				log.debug("revoking user '{}' rights on object (" + object.getClass() + "): {}",
						((PrincipalSid) sid).getPrincipal(), Permissions.getByMask(entry.getPermission().getMask()));
				acl.deleteAce(i);
			}
		}
		aclService.updateAcl(acl);
	}

	@Override
	public <T extends AclAllowedClass> void revokeRightsOnObject(T object, String username,
			List<Permission> permissions) {
		Sid sid = new PrincipalSid(username);
		this.revokeRightsOnObject(object, sid, permissions);
	}

	@Override
	public <T> void removeEntriesOnDelete(T object) {
		ObjectIdentity oi = new ObjectIdentityImpl(object);

		MutableAcl acl = null;
		try {
			acl = (MutableAcl) aclService.readAclById(oi);
		} catch (NotFoundException nfe) {
			log.warn("object deleted, no acls found...");
			return;
		}

		aclService.deleteAcl(acl.getObjectIdentity(), true);
	}

	@Override
	public <T extends AclAllowedClass> boolean hasRight(T object, String username, Collection<Permission> permissions) {
		ObjectIdentity oi = new ObjectIdentityImpl(object);
		Sid sid = new PrincipalSid(username);

		MutableAcl acl = null;
		try {
			acl = (MutableAcl) aclService.readAclById(oi);
		} catch (NotFoundException nfe) {
			return false;
		}
		return checkGranted(acl, sid, permissions);
	}

	private boolean checkGranted(MutableAcl acl, Sid sid, Collection<Permission> permissions) {
		boolean isGranted = false;
		try {
			isGranted = acl.isGranted(permissions.stream().collect(Collectors.toList()), Arrays.asList(sid), false);
		} catch (Exception e) {
			isGranted = false;
		}
		return isGranted;
	}

	@Override
	public <T extends AclAllowedClass> List<GrantDTO> getRights(T object) {
		ObjectIdentity oi = new ObjectIdentityImpl(object);

		MutableAcl acl = null;
		acl = (MutableAcl) aclService.readAclById(oi);

		List<AccessControlEntry> entries = acl.getEntries();
		Map<String, Set<String>> map = new HashMap<>();
		for (AccessControlEntry entry : entries) {
			String permission = Permissions.getByMask(entry.getPermission().getMask());
			PrincipalSid sid = (PrincipalSid) entry.getSid();
			if (!map.containsKey(sid.getPrincipal())) {
				map.put(sid.getPrincipal(), new HashSet<>());
			}
			map.get(sid.getPrincipal()).add(permission);
		}
		List<GrantDTO> grants = new ArrayList<>();

		for (String principal : map.keySet()) {
			grants.add(new GrantDTO(principal, map.get(principal)));
		}
		return grants;
	}

}
