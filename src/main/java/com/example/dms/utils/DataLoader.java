package com.example.dms.utils;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import com.example.dms.services.FolderService;

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
	
	@Autowired
	FolderService folderService;
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
    @Override
	public void run(ApplicationArguments args) {
    	
    	DmsFolder root = null;
    	if (folderRepository.findByPath("/").isEmpty())
    		root = folderRepository.save(DmsFolder.builder().path("/").build());
    	
    	// frontend test required
    	DmsFolder child1 = null;
    	if (folderRepository.findByPath("/child1").isEmpty())
    		child1 = folderRepository.save(DmsFolder.builder().path("/child1").parentFolder(root).build());
    	DmsFolder child2 = null;
    	if (folderRepository.findByPath("/child2").isEmpty())
    		child2 = folderRepository.save(DmsFolder.builder().path("/child2").parentFolder(root).build());
    	
    	DmsFolder grandchild1 = null;
    	if (folderRepository.findByPath("/child1/grandchild1").isEmpty())
    		grandchild1 = folderRepository.save(DmsFolder.builder().path("/child1/grandchild1").parentFolder(child1).build());
    	
    	DmsFolder grandchild2 = null;
    	if (folderRepository.findByPath("/child1/grandchild2").isEmpty())
    		grandchild2 = folderRepository.save(DmsFolder.builder().path("/child1/grandchild2").parentFolder(child1).build());
    	
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
    	String dummyPassword = passwordEncoder.encode("12345");
    	DmsUser user = DmsUser.builder().username("user").password(dummyPassword).firstName("userF").lastName("userL").email("user.user@gmail.com")
    			.roles(Arrays.asList(userRole)).build();	
    	if (userRepository.findByUsername("user").isEmpty()) {
    		userRepository.save(user);
    	} 
    	DmsUser editor = DmsUser.builder().username("editor").password(dummyPassword).firstName("editorF").lastName("editorL").email("editor.editor@gmail.com")
    			.roles(Arrays.asList(editorRole)).build();	
    	if (userRepository.findByUsername("editor").isEmpty()) {
    		userRepository.save(editor);
    	} 
    	DmsUser creator = DmsUser.builder().username("creator").password(dummyPassword).firstName("creatorF").lastName("creatorL").email("creator.creator@gmail.com")
    			.roles(Arrays.asList(creatorRole)).build();	
    	if (userRepository.findByUsername("creator").isEmpty()) {
    		userRepository.save(creator);
    	} 
    	DmsUser admin = DmsUser.builder().username("admin").password(dummyPassword).firstName("adminF").lastName("adminL").email("admin.admin@gmail.com")
    			.roles(Arrays.asList(adminRole)).build();	
    	if (userRepository.findByUsername("admin").isEmpty()) {
    		userRepository.save(admin);
    	} 
    }
}