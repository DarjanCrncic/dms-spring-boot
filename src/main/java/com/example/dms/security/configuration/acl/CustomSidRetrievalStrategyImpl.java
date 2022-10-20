package com.example.dms.security.configuration.acl;

import com.example.dms.security.DmsUserDetails;
import org.springframework.security.access.hierarchicalroles.NullRoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomSidRetrievalStrategyImpl implements SidRetrievalStrategy {

	private RoleHierarchy roleHierarchy = new NullRoleHierarchy();

	public CustomSidRetrievalStrategyImpl() {
	}

	public CustomSidRetrievalStrategyImpl(RoleHierarchy roleHierarchy) {
		Assert.notNull(roleHierarchy, "RoleHierarchy must not be null");
		this.roleHierarchy = roleHierarchy;
	}

	@Override
	public List<Sid> getSids(Authentication authentication) {
		Collection<? extends GrantedAuthority> authorities = this.roleHierarchy
				.getReachableGrantedAuthorities(authentication.getAuthorities());
		List<Sid> sids = new ArrayList<>(authorities.size() + 1);
		sids.add(new PrincipalSid(authentication));
		for (GrantedAuthority authority : authorities) {
			sids.add(new GrantedAuthoritySid(authority));
		}

		List<Sid> groupSids = ((DmsUserDetails) authentication.getPrincipal()).getGroupIdentifiers().stream()
				.map(PrincipalSid::new).collect(Collectors.toList());
		sids.addAll(groupSids);

		return sids;
	}
}

