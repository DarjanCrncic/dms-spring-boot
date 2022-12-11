package com.example.dms.services.impl;

import com.example.dms.domain.DmsNotification;
import com.example.dms.repositories.NotificationRepository;
import com.example.dms.services.MessagingService;
import com.example.dms.services.NotificationService;
import com.example.dms.utils.exceptions.DmsNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final MessagingService messagingService;

	@Override
	public DmsNotification findById(UUID id) {
		return notificationRepository.findById(id).orElseThrow(DmsNotFoundException::new);
	}

	@Override
	public DmsNotification save(DmsNotification notification) {
		messagingService.notify(notification);
		return notificationRepository.save(notification);
	}

	@Override
	public List<DmsNotification> getAllForUser(UUID userId) {
		Sort sort = Sort.by(Sort.Direction.DESC, "creationDate");
		return notificationRepository.findAllByRecipientIdIn(Collections.singletonList(userId), sort);
	}

	@Override
	public void deleteAllForUser(UUID userId) {
		notificationRepository.deleteAllByRecipientIdIn(Collections.singletonList(userId));
	}

	@Override
	public DmsNotification markAsSeen(UUID id) {
		DmsNotification notification = this.findById(id);
		notification.setSeen(true);
		return notificationRepository.save(notification);
	}
}
