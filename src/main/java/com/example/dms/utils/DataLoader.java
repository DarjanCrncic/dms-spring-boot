package com.example.dms.utils;

import java.util.Arrays;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.dms.domain.DmsFolder;
import com.example.dms.domain.DmsType;
import com.example.dms.domain.DmsUser;
import com.example.dms.domain.security.DmsPrivilege;
import com.example.dms.domain.security.DmsRole;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.repositories.TypeRepository;
import com.example.dms.repositories.UserRepository;
import com.example.dms.repositories.security.PrivilegeRepository;
import com.example.dms.repositories.security.RoleRepository;
import com.example.dms.services.DocumentService;
import com.example.dms.services.FolderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

	private final UserRepository userRepository;
	private final FolderRepository folderRepository;
	private final RoleRepository roleRepository;
	private final PrivilegeRepository privilegeRepository;
	private final DocumentService documentService;
	private final FolderService folderService;
	private final BCryptPasswordEncoder passwordEncoder;
	private final TypeRepository typeRepository;
	
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
    	
    	DmsPrivilege read = privilegeRepository.findByName(Privileges.READ_PRIVILEGE.name()).orElse(null);
    	if (read == null) {
    		read = privilegeRepository.save(DmsPrivilege.builder().name(Privileges.READ_PRIVILEGE.name()).build());
    	}
    	DmsPrivilege write = privilegeRepository.findByName(Privileges.WRITE_PRIVILEGE.name()).orElse(null);
    	if (write == null) {
    		write = privilegeRepository.save(DmsPrivilege.builder().name(Privileges.WRITE_PRIVILEGE.name()).build());
    	}
    	DmsPrivilege create = privilegeRepository.findByName(Privileges.CREATE_PRIVILEGE.name()).orElse(null);
    	if (create == null) {
    		create = privilegeRepository.save(DmsPrivilege.builder().name(Privileges.CREATE_PRIVILEGE.name()).build());
    	}
    	DmsPrivilege version = privilegeRepository.findByName(Privileges.VERSION_PRIVILEGE.name()).orElse(null);
    	if (version == null) {
    		version = privilegeRepository.save(DmsPrivilege.builder().name(Privileges.VERSION_PRIVILEGE.name()).build());
    	}
    	DmsPrivilege permission = privilegeRepository.findByName(Privileges.PERMISSION_PRIVILEGE.name()).orElse(null);
    	if (permission == null) {
    		permission = privilegeRepository.save(DmsPrivilege.builder().name(Privileges.PERMISSION_PRIVILEGE.name()).build());
    	}
    	DmsPrivilege delete = privilegeRepository.findByName(Privileges.DELETE_PRIVILEGE.name()).orElse(null);
    	if (delete == null) {
    		delete = privilegeRepository.save(DmsPrivilege.builder().name(Privileges.DELETE_PRIVILEGE.name()).build());
    	}
    	
    	// create roles
    	DmsRole adminRole = null;
    	if (roleRepository.findByName(Roles.ROLE_ADMIN.name()).isEmpty()) {
    		adminRole = roleRepository.save(DmsRole.builder().name(Roles.ROLE_ADMIN.name()).privileges(
    				Arrays.asList(delete,read,write,version,create,permission)
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
    	DmsUser admin = DmsUser.builder().username("admin").password(dummyPassword).firstName("adminF").lastName("adminL").email("admin.admin@gmail.com")
    			.roles(Arrays.asList(adminRole)).build();	
    	if (userRepository.findByUsername("admin").isEmpty()) {
    		userRepository.save(admin);
    	} 
    	
    	if (!typeRepository.existsByTypeName("document")) {
    		typeRepository.save(DmsType.builder().typeName("document").build());
    	}
    }
}