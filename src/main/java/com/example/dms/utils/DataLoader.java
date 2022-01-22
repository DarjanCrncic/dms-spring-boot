package com.example.dms.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsFolder;
import com.example.dms.domain.DmsUser;
import com.example.dms.services.DocumentService;
import com.example.dms.services.FolderService;
import com.example.dms.services.UserService;

@Component
public class DataLoader implements ApplicationRunner {

	@Autowired
	UserService userService;
	
	@Autowired
	DocumentService documentService;
	
	@Autowired
	FolderService folderService;
	
    @Override
	public void run(ApplicationArguments args) {
    	System.out.println("hello world");
    	DmsUser user = DmsUser.builder().username("dcrncic").password("12345").firstName("Darjan").lastName("Crnčić").email("darjan.crncic@gmail.com").build();	
    	userService.save(user);
    	
    	DmsDocument doc1 = DmsDocument.builder().creator(user).objectName("test1").build();
    	DmsDocument doc2 = DmsDocument.builder().creator(user).objectName("test2").build();
    	
    	documentService.save(doc1);
    	documentService.save(doc2);
    	
    	System.out.println(documentService.findById(doc1.getId()).toString());

    	folderService.save(DmsFolder.builder().path("/").build());
//    	folderService.createNewFolder("/test");
//		folderService.createNewFolder("/test/test1");
    }
}