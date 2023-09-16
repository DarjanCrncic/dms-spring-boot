package com.example.dms.repositories;

import com.example.dms.domain.DmsNotification;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<DmsNotification, Integer> {
	List<DmsNotification> findAllByRecipientIdIn(List<Integer> arrayList, Sort sort);
	void deleteAllByRecipientIdIn(List<Integer> singletonList);
}
