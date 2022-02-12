package com.example.dms.services;

import com.example.dms.domain.DmsDocument;

public interface DmsAclService {

	void grantCreatorRightsOnDocument(DmsDocument document, String username);

}
