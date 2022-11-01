package com.example.dms.services.impl;

import com.example.dms.api.mappers.NotificationMapper;
import com.example.dms.domain.DmsNotification;
import com.example.dms.services.MessagingService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagingServiceImpl implements MessagingService {

	private final SimpMessagingTemplate simpMessagingTemplate;
	private final NotificationMapper notificationMapper;
	private final TaskExecutor taskExecutor;


	@Override
	public void notify(DmsNotification notification) {
		NotificationRunnable runnable = new NotificationRunnable(notification);
		taskExecutor.execute(new DelegatingSecurityContextRunnable(runnable));
	}

	@AllArgsConstructor
	private class NotificationRunnable implements Runnable {

		private DmsNotification notification;

		@Override
		public void run() {
			simpMessagingTemplate.convertAndSend("/documents", notificationMapper.entityToDto(notification));
		}
	}
}
