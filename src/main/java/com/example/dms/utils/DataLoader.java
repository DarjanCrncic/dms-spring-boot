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
    	DmsPrivilege delete = privilegeRepository.findByName(Privileges.DELETE_PRIVILEGE.name()).orElse(null);
    	if (delete == null) {
    		delete = privilegeRepository.save(DmsPrivilege.builder().name(Privileges.DELETE_PRIVILEGE.name()).build());
    	}
    	DmsPrivilege read = privilegeRepository.findByName(Privileges.READ_PRIVILEGE.name()).orElse(null);
    	if (read == null) {
    		read = privilegeRepository.save(DmsPrivilege.builder().name(Privileges.READ_PRIVILEGE.name()).build());
    	}
    	DmsPrivilege write = privilegeRepository.findByName(Privileges.WRITE_PRIVILEGE.name()).orElse(null);
    	if (write == null) {
    		write = privilegeRepository.save(DmsPrivilege.builder().name(Privileges.WRITE_PRIVILEGE.name()).build());
    	}
    	DmsPrivilege version = privilegeRepository.findByName(Privileges.VERSION_PRIVILEGE.name()).orElse(null);
    	if (version == null) {
    		version = privilegeRepository.save(DmsPrivilege.builder().name(Privileges.VERSION_PRIVILEGE.name()).build());
    	}
    	DmsPrivilege create = privilegeRepository.findByName(Privileges.CREATE_PRIVILEGE.name()).orElse(null);
    	if (create == null) {
    		create = privilegeRepository.save(DmsPrivilege.builder().name(Privileges.CREATE_PRIVILEGE.name()).build());
    	}
    	
    	// create roles
    	DmsRole adminRole = null;
    	if (roleRepository.findByName(Roles.ROLE_ADMIN.name()).isEmpty()) {
    		adminRole = roleRepository.save(DmsRole.builder().name(Roles.ROLE_ADMIN.name()).privileges(
    				Arrays.asList(delete,read,write,version,create)
    				).build());
    	}
    	DmsRole creatorRole = null;
    	if (roleRepository.findByName(Roles.ROLE_CREATOR.name()).isEmpty()) {
    		creatorRole = roleRepository.save(DmsRole.builder().name(Roles.ROLE_CREATOR.name()).privileges(
    				Arrays.asList(read,write,version,create)
    				).build());
    	}
    	DmsRole editorRole = null;
    	if (roleRepository.findByName(Roles.ROLE_EDITOR.name()).isEmpty()) {
    		editorRole = roleRepository.save(DmsRole.builder().name(Roles.ROLE_EDITOR.name()).privileges(
    				Arrays.asList(read,write)
    				).build());
    	}
    	DmsRole userRole = null;
    	if (roleRepository.findByName(Roles.ROLE_USER.name()).isEmpty()) {
    		userRole = roleRepository.save(DmsRole.builder().name(Roles.ROLE_USER.name()).privileges(
    				Arrays.asList(read)
    				).build());
    	}
    	
    	// test and admin users
    	DmsUser user = DmsUser.builder().username("user").password("12345").firstName("user").lastName("user").email("user.user@gmail.com")
    			.roles(Arrays.asList(userRole)).build();	
    	if (userRepository.findByUsername("user").isEmpty()) {
    		userRepository.save(user);
    	} 
    	DmsUser editor = DmsUser.builder().username("editor").password("12345").firstName("editor").lastName("editor").email("editor.editor@gmail.com")
    			.roles(Arrays.asList(editorRole)).build();	
    	if (userRepository.findByUsername("editor").isEmpty()) {
    		userRepository.save(editor);
    	} 
    	DmsUser creator = DmsUser.builder().username("creator").password("12345").firstName("creator").lastName("creator").email("creator.creator@gmail.com")
    			.roles(Arrays.asList(creatorRole)).build();	
    	if (userRepository.findByUsername("creator").isEmpty()) {
    		userRepository.save(creator);
    	} 
    	DmsUser admin = DmsUser.builder().username("admin").password("12345").firstName("admin").lastName("admin").email("admin.admin@gmail.com")
    			.roles(Arrays.asList(adminRole)).build();	
    	if (userRepository.findByUsername("dmsadmin").isEmpty()) {
    		userRepository.save(admin);
    	} 
    }
}