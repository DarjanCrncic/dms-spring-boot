package com.example.dms.services.impl;

import com.example.dms.api.dtos.administration.GrantDTO;
import com.example.dms.api.dtos.administration.NotificationMessage;
import com.example.dms.api.dtos.document.DmsDocumentDTO;
import com.example.dms.services.AdministrationService;
import com.example.dms.services.MessagingService;
import com.example.dms.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessagingServiceImpl implements MessagingService {

	private final SimpMessagingTemplate simpMessagingTemplate;
	private final AdministrationService administrationService;

	@Override
	public void sendDocumentNotification(DmsDocumentDTO documentDTO) {
		NotificationMessage message = new NotificationMessage();
		message.setMessage("Document " + documentDTO.getObjectName() + " has been updated.");
		message.setReceivers(administrationService.getRightsForDocument(
				documentDTO.getId()).stream().map(GrantDTO::getUsername).collect(Collectors.toSet()));
		message.setLinkTo(documentDTO.getParentFolderId());
		message.setTimestamp(StringUtils.dateTimeToString(documentDTO.getModifyDate()));

		simpMessagingTemplate.convertAndSend("/documents", message);
	}
}
