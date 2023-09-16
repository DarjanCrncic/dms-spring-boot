package com.example.dms.services;

import com.example.dms.domain.DmsNotification;
import com.example.dms.domain.interfaces.DmsAclNotifiable;
import com.example.dms.utils.ActionEnum;

import java.util.List;

public interface NotificationService {
	DmsNotification findById(Integer id);

	DmsNotification save(DmsNotification notification);

	List<DmsNotification> getAllForUser(Integer userId);

	void deleteAllForUser(Integer userId);

	DmsNotification markAsSeen(Integer id);

	void createAclNotification(DmsAclNotifiable object, ActionEnum action);
}
