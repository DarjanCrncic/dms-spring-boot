package com.example.dms.utils;

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
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

	private final UserRepository userRepository;
	private final FolderRepository folderRepository;
	private final RoleRepository roleRepository;
	private final PrivilegeRepository privilegeRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final TypeRepository typeRepository;

    @Override
	public void run(ApplicationArguments args) {

    	if (folderRepository.findByName("/").isEmpty())
    		folderRepository.save(DmsFolder.builder().name("/").build());

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
    	DmsPrivilege administration = privilegeRepository.findByName(Privileges.ADMINISTRATION_PRIVILEGE.name()).orElse(null);
    	if (administration == null) {
    		administration = privilegeRepository.save(DmsPrivilege.builder().name(Privileges.ADMINISTRATION_PRIVILEGE.name()).build());
    	}
    	DmsPrivilege delete = privilegeRepository.findByName(Privileges.DELETE_PRIVILEGE.name()).orElse(null);
    	if (delete == null) {
    		delete = privilegeRepository.save(DmsPrivilege.builder().name(Privileges.DELETE_PRIVILEGE.name()).build());
    	}

    	// create roles
    	DmsRole adminRole = roleRepository.findByName(Roles.ROLE_ADMIN.name()).orElse(null);
    	if (adminRole == null) {
    		adminRole = roleRepository.save(DmsRole.builder().name(Roles.ROLE_ADMIN.name()).build());
    	}
    	DmsRole userRole = roleRepository.findByName(Roles.ROLE_USER.name()).orElse(null);
    	if (userRole == null) {
    		userRole = roleRepository.save(DmsRole.builder().name(Roles.ROLE_USER.name()).build());
    	}

		// create types
		if (!typeRepository.existsByTypeName("document")) {
			typeRepository.save(DmsType.builder().typeName("document").build());
		}

		// test and admin users
		String dummyPassword = passwordEncoder.encode("12345");
		DmsUser user = DmsUser.builder()
				.username("user")
				.password(dummyPassword)
				.firstName("userF")
				.lastName("userL")
				.email("user.user@gmail.com")
    			.roles(Utils.toSet(userRole))
				.privileges(Collections.emptySet()).build();
		if (userRepository.findByUsername("user").isEmpty()) {
			userRepository.save(user);
		}

		DmsUser test = DmsUser.builder()
				.username("tester")
				.password(dummyPassword)
				.firstName("testF")
				.lastName("testL")
				.email("tester.tester@gmail.com")
    			.roles(Utils.toSet(userRole))
				.privileges(Utils.toSet(read, write, create)).build();
		if (userRepository.findByUsername("tester").isEmpty()) {
			userRepository.save(test);
		}

		DmsUser admin = DmsUser.builder()
				.username("admin")
				.password(dummyPassword)
				.firstName("adminF")
				.lastName("adminL")
				.email("admin.admin@gmail.com")
    			.roles(Utils.toSet(adminRole))
				.privileges(Utils.toSet(read, write, create, version, administration, delete)).build();
    	if (userRepository.findByUsername("admin").isEmpty()) {
    		userRepository.save(admin);
    	}
    }
}