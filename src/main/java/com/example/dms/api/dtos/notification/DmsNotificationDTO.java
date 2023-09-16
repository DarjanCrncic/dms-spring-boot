package com.example.dms.api.dtos.notification;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DmsNotificationDTO {
	private Integer id;
	private LocalDateTime creationDate;
	private String message;
	private Integer linkTo;
	private boolean seen = false;
	private String recipient;
}
