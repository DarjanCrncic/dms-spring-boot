package com.example.dms.services;

import com.example.dms.domain.DmsNotification;
import com.example.dms.domain.interfaces.DmsAclNotifiable;
import com.example.dms.utils.ActionEnum;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
	DmsNotification findById(UUID id);

	DmsNotification save(DmsNotification notification);

	List<DmsNotification> getAllForUser(UUID userId);

	void deleteAllForUser(UUID userId);

	DmsNotification markAsSeen(UUID id);

	void createAclNotification(DmsAclNotifiable object, ActionEnum action);
}
