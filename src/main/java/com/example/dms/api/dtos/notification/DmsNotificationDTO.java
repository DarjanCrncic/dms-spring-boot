package com.example.dms.api.dtos.notification;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DmsNotificationDTO {
	private UUID id;
	private LocalDateTime creationDate;
	private String message;
	private UUID linkTo;
	private boolean seen = false;
	private List<String> recipients;
}
