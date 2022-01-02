package com.example.dms.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.example.dms.domain.Document;
import com.example.dms.domain.User;
import com.example.dms.services.DocumentService;
import com.example.dms.services.UserService;

@Component
public class DataLoader implements ApplicationRunner {

	@Autowired
	UserService userService;
	
	@Autowired
	DocumentService documentService;
	
    @Override
	public void run(ApplicationArguments args) {
    	System.out.println("hello world");
    	User user = new User("dcrncic", "12345", "Darjan", "Crnčić","darjan.crncic@gmail.com");	
    	userService.save(user);
    	
    	Document doc1 = new Document(user, "test1");
    	Document doc2 = new Document(user, "test2");
    	
    	documentService.save(doc1);
    	documentService.save(doc2);
    	
    	System.out.println(documentService.findById(doc1.getId()).toString());
    }
}