package com.example.dms.utils;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.example.dms.domain.DmsFolder;
import com.example.dms.domain.DmsUser;
import com.example.dms.domain.security.DmsPrivilege;
import com.example.dms.domain.security.DmsRole;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.repositories.UserRepository;
import com.example.dms.repositories.security.PrivilegeRepository;
import com.example.dms.repositories.security.RoleRepository;
import com.example.dms.services.DocumentService;

@Component
public class DataLoader implements ApplicationRunner {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	FolderRepository folderRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	PrivilegeRepository privilegeRepository;
	
	@Autowired
	DocumentService documentService;
	
    @Override
	public void run(ApplicationArguments args) {
    	
    	
    	if (folderRepository.findByPath("/").isEmpty())
    		folderRepository.save(DmsFolder.builder().path("/").build());
    	
    	// create privileges
    	DmsPrivilege delete = null, read = null, write = null, version = null, create = null;
    	if (privilegeRepository.findByName(Privileges.DELETE.name()).isEmpty()) {
    		delete = privilegeRepository.save(DmsPrivilege.builder().name(Privileges.DELETE.name()).build());
    	}
    	if (privilegeRepository.findByName(Privileges.READ.name()).isEmpty()) {
    		read = privilegeRepository.save(DmsPrivilege.builder().name(Privileges.READ.name()).build());
    	}
    	if (privilegeRepository.findByName(Privileges.WRITE.name()).isEmpty()) {
    		write = privilegeRepository.save(DmsPrivilege.builder().name(Privileges.WRITE.name()).build());
    	}
    	if (privilegeRepository.findByName(Privileges.VERSION.name()).isEmpty()) {
    		version = privilegeRepository.save(DmsPrivilege.builder().name(Privileges.VERSION.name()).build());
    	}
    	if (privilegeRepository.findByName(Privileges.CREATE.name()).isEmpty()) {
    		create = privilegeRepository.save(DmsPrivilege.builder().name(Privileges.CREATE.name()).build());
    	}
    	
    	// create roles
    	DmsRole adminRole = null;
    	if (roleRepository.findByName(Roles.ROLE_ADMIN.name()).isEmpty()) {
    		adminRole = roleRepository.save(DmsRole.builder().name(Roles.ROLE_ADMIN.name()).privileges(
    				Arrays.asList(delete,read,write,version,create)
    				).build());
    	}

    	if (roleRepository.findByName(Roles.ROLE_CREATOR.name()).isEmpty()) {
    		roleRepository.save(DmsRole.builder().name(Roles.ROLE_CREATOR.name()).privileges(
    				Arrays.asList(read,write,version,create)
    				).build());
    	}

    	if (roleRepository.findByName(Roles.ROLE_EDITOR.name()).isEmpty()) {
    		roleRepository.save(DmsRole.builder().name(Roles.ROLE_EDITOR.name()).privileges(
    				Arrays.asList(read,write,version)
    				).build());
    	}
    	DmsRole userRole = null;
    	if (roleRepository.findByName(Roles.ROLE_USER.name()).isEmpty()) {
    		userRole = roleRepository.save(DmsRole.builder().name(Roles.ROLE_USER.name()).privileges(
    				Arrays.asList(read)
    				).build());
    	}
    	
    	// test and admin users
    	DmsUser user = DmsUser.builder().username("user").password("12345").firstName("user").lastName("user").email("user.user@gmail.com").roles(Arrays.asList(userRole)).build();	
    	if (userRepository.findByUsername("user").isEmpty()) {
    		userRepository.save(user);
    	} 
    	
    	DmsUser adminUser = DmsUser.builder().username("dmsadmin").password("dmsadmin").firstName("dmsadmin").lastName("dmsadmin").email("dms.admin@gmail.com").roles(Arrays.asList(adminRole)).build();	
    	if (userRepository.findByUsername("dmsadmin").isEmpty()) {
    		userRepository.save(adminUser);
    	} 

    }
}