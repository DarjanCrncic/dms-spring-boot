package com.example.dms.services;

import com.example.dms.api.dtos.document.DmsDocumentDTO;

public interface MessagingService {
	void sendDocumentNotification(DmsDocumentDTO documentDTO);
}
