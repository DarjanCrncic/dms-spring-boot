package com.example.dms.services.impl;

import org.springframework.stereotype.Service;

import com.example.dms.domain.Document;
import com.example.dms.services.DocumentService;

@Service
public class DocumentServiceImpl extends EntityCrudServiceImpl<Document> implements DocumentService{

}
