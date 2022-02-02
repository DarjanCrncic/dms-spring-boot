package com.example.dms.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.example.dms.domain.DmsFolder;
import com.example.dms.domain.DmsUser;
import com.example.dms.repositories.FolderRepository;
import com.example.dms.repositories.UserRepository;

@Component
public class DataLoader implements ApplicationRunner {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	FolderRepository folderRepository;
	
    @Override
	public void run(ApplicationArguments args) {
    	
    	DmsUser user = DmsUser.builder().username("user").password("12345").firstName("user").lastName("user").email("user.user@gmail.com").build();	
    	if (userRepository.findByUsername("user").isEmpty()) {
    		userRepository.save(user);
    	} 
    	
    	if (folderRepository.findByPath("/").isEmpty())
    		folderRepository.save(DmsFolder.builder().path("/").build());
    }
}