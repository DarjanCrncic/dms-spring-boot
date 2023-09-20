package com.example.dms.api.controllers;

import com.example.dms.api.dtos.notification.DmsNotificationDTO;
import com.example.dms.api.mappers.NotificationMapper;
import com.example.dms.security.DmsUserDetails;
import com.example.dms.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class NotificationController {

	private final NotificationService notificationService;
	private final NotificationMapper notificationMapper;

	@GetMapping
	public List<DmsNotificationDTO> getAllForUser(@AuthenticationPrincipal DmsUserDetails dmsUserDetails) {
		return notificationMapper.entityListToDtoList(notificationService.getAllForUser(dmsUserDetails.getUser().getId()));
	}

	@DeleteMapping
	public void clearAllForUser(@AuthenticationPrincipal DmsUserDetails dmsUserDetails) {
		notificationService.deleteAllForUser(dmsUserDetails.getUser().getId());
	}
}
