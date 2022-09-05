package com.example.dms.security;

import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class DmsUserDetailsService implements UserDetailsService {
	
	@Autowired
	UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		DmsUser user = userRepository.findByUsername(username).orElse(null);
		
		if (user == null) {
			throw new UsernameNotFoundException("User with username: " + username + " is not found.");
		}
		return new DmsUserDetails(user, getPrivileges(user));
	}
	

	private List<GrantedAuthority> getPrivileges(DmsUser user) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.addAll(user.getRoles());
		authorities.addAll(user.getPrivileges());
		return authorities;
	}

}
