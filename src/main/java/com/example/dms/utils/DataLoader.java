package com.example.dms.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.example.dms.domain.DmsFolder;
import com.example.dms.domain.DmsDocument;
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
    	DmsUser user = new DmsUser("dcrncic", "12345", "Darjan", "Crnčić","darjan.crncic@gmail.com", null);	
    	userService.save(user);
    	
    	DmsDocument doc1 = new DmsDocument(user, "test1");
    	DmsDocument doc2 = new DmsDocument(user, "test2");
    	
    	documentService.save(doc1);
    	documentService.save(doc2);
    	
    	System.out.println(documentService.findById(doc1.getId()).toString());

    	folderService.save(DmsFolder.builder().path("/").build());
//    	folderService.createNewFolder("/test");
//		folderService.createNewFolder("/test/test1");
    }
}