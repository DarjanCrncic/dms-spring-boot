package com.example.dms.repositories;

import com.example.dms.domain.DmsNotification;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<DmsNotification, UUID> {
	List<DmsNotification> findAllByRecipientIdIn(List<UUID> arrayList, Sort sort);
	void deleteAllByRecipientIdIn(List<UUID> singletonList);
}
