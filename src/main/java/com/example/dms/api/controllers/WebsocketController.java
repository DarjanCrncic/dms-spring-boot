package com.example.dms.api.controllers;

import com.example.dms.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class WebsocketController {

	private final NotificationService notificationService;

	@MessageMapping("/notifications/register")
	public void greeting(String notificationId) throws Exception {
		notificationService.markAsSeen(UUID.fromString(notificationId));
	}
}
