package com.example.dms.api.dtos.administration;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class NotificationMessage {
	private String message;
	private Set<String> receivers;
	private UUID linkTo;
	private String timestamp;
}
