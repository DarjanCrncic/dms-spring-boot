package com.example.dms.services.impl;

import com.example.dms.api.dtos.administration.GrantDTO;
import com.example.dms.domain.security.AclAllowedClass;
import com.example.dms.security.DmsUserDetailsService;
import com.example.dms.services.DmsAclService;
import com.example.dms.utils.Permissions;
import com.example.dms.utils.Roles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DmsAclServiceImpl implements DmsAclService {

	private final JdbcMutableAclService aclService;
	private final DmsUserDetailsService userDetailsService;

	@Override
	public <T extends AclAllowedClass> void grantRightsOnObject(T object, String username, Collection<Permission> permissions) {
		if (isUserAdmin(username)) return;
		Sid sid = new PrincipalSid(username);
		this.grantRightsOnObject(object, sid, permissions);
	}

	private boolean isUserAdmin(String username) {
		UserDetails details = userDetailsService.loadUserByUsername(username);
		return details.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList()).contains(Roles.ROLE_ADMIN.name());
	}

	@Override
	public <T extends AclAllowedClass> void grantRightsOnObject(T object, Sid sid, Collection<Permission> permissions) {
		if (isUserAdmin(((PrincipalSid) sid).getPrincipal())) return; // TODO: might fail if i introduce groups
		ObjectIdentity oi = new ObjectIdentityImpl(object);
		MutableAcl acl;
		try {
			acl = (MutableAcl) aclService.readAclById(oi);
		} catch (NotFoundException nfe) {
			acl = aclService.createAcl(oi);
		}
		log.debug("granting '{}' rights on object (" + object.getClass() + "): {}", ((PrincipalSid) sid).getPrincipal(),
				Permissions.getByMasks(permissions.stream().map(Permission::getMask).collect(Collectors.toList())));

		for (Permission permission : permissions) {
			acl.insertAce(acl.getEntries().size(), permission, sid, true);
		}
		aclService.updateAcl(acl);
	}

	@Override
	public <T extends AclAllowedClass> void revokeRightsOnObject(T object, Sid sid, List<Permission> permissions) {
		ObjectIdentity oi = new ObjectIdentityImpl(object);

		MutableAcl acl;
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

		MutableAcl acl;
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
		if (isUserAdmin(username)) return true;
		ObjectIdentity oi = new ObjectIdentityImpl(object);
		Sid sid = new PrincipalSid(username);

		MutableAcl acl;
		try {
			acl = (MutableAcl) aclService.readAclById(oi);
		} catch (NotFoundException nfe) {
			return false;
		}
		return checkGranted(acl, sid, permissions);
	}

	private boolean checkGranted(MutableAcl acl, Sid sid, Collection<Permission> permissions) {
		boolean isGranted;
		try {
			isGranted = acl.isGranted(new ArrayList<>(permissions), List.of(sid), false);
		} catch (Exception e) {
			isGranted = false;
		}
		return isGranted;
	}

	@Override
	public <T extends AclAllowedClass> List<GrantDTO> getRights(T object) {
		ObjectIdentity oi = new ObjectIdentityImpl(object);

		MutableAcl acl;
		try {
			acl = (MutableAcl) aclService.readAclById(oi);
		} catch (NotFoundException nfe) {
			return Collections.emptyList();
		}

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
	
	@Override
	public <T extends AclAllowedClass> void copyRightsToAnotherEntity(T original, T copy) {
		ObjectIdentity originalOI = new ObjectIdentityImpl(original);
		
		MutableAcl acl;
		try {
			acl = (MutableAcl) aclService.readAclById(originalOI);
		} catch (NotFoundException nfe) {
			log.warn("object invalid, no acls found...");
			return;
		}
		
		List<AccessControlEntry> entries = acl.getEntries();
		Map<Sid, Set<Permission>> map = new HashMap<>();
		for (AccessControlEntry entry : entries) {
			Sid sid = entry.getSid();
			if (!map.containsKey(sid)) {
				map.put(sid, new HashSet<>());
			}
			map.get(sid).add(entry.getPermission());
		}
		
		for (Sid sid : map.keySet()) {
			grantRightsOnObject(copy, sid, map.get(sid));
		}
	}

}
