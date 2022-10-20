package com.example.dms.security;

import com.example.dms.domain.DmsGroup;
import com.example.dms.domain.DmsUser;
import com.example.dms.domain.security.DmsPrivilege;
import com.example.dms.domain.security.DmsRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DmsUserDetails implements UserDetails{

	private static final long serialVersionUID = 6539177333720949995L;

	private final DmsUser dmsUser;
	
	private Collection<? extends GrantedAuthority> authorities;
	
	public DmsUserDetails(DmsUser dmsUser, Collection<? extends GrantedAuthority> authorities) {
		this.dmsUser = dmsUser;
		this.authorities = authorities;
	}
	
	@Override
	public String getPassword() {
		return dmsUser.getPassword();
	}

	@Override
	public String getUsername() {
		return dmsUser.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return dmsUser.isEnabled();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	public DmsUser getUser() {
		return this.dmsUser;
	}
	
	public List<String> getPrivileges() {
		return dmsUser.getPrivileges().stream().map(DmsPrivilege::getAuthority).collect(Collectors.toList());
	}
	
	public List<String> getRoles() {
		return dmsUser.getRoles().stream().map(DmsRole::getAuthority).collect(Collectors.toList());
	}

	public List<String> getGroupIdentifiers() {
		return dmsUser.getGroups().stream().map(DmsGroup::getIdentifier).collect(Collectors.toList());
	}
} 