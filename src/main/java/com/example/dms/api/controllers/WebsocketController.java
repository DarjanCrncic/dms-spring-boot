package com.example.dms.api.controllers;

import com.example.dms.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WebsocketController {

	private final NotificationService notificationService;

	@MessageMapping("/notifications/register")
	public void registerNotification(String notificationId) {
		notificationService.markAsSeen(Integer.parseInt(notificationId));
	}
}
