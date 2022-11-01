package com.example.dms.services;

import com.example.dms.domain.DmsNotification;

public interface MessagingService {
	void notify(DmsNotification notification);
}
