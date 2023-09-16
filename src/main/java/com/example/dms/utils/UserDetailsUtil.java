package com.example.dms.utils;

import com.example.dms.domain.DmsUser;
import com.example.dms.security.DmsUserDetails;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Log4j2
public class UserDetailsUtil {

	public static DmsUserDetails extractDetailsFromPrincipal(Object principal, Collection<? extends GrantedAuthority> authorities) {
		if (principal instanceof DmsUser) {
			log.warn("Generating DmsUserDetails from DmsUser class, username: " + ((DmsUser) principal).getUsername());
			return new DmsUserDetails((DmsUser) principal, authorities != null ? authorities : ((DmsUser) principal).getPrivileges());
		} else if (principal instanceof User) {
			// case when @WithMockUser is used
			log.warn("Generating DmsUserDetails from User class, username: " + ((User) principal).getUsername());
			DmsUser dmsUser = new DmsUser();
			dmsUser.setUsername(((User) principal).getUsername());
			return new DmsUserDetails(dmsUser, authorities);
		} else {
			return (DmsUserDetails) principal;
		}
	}
}
