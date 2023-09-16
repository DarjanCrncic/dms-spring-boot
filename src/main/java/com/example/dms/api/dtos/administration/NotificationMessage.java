package com.example.dms.api.dtos.administration;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class NotificationMessage {
	private String message;
	private Set<String> receivers;
	private Integer linkTo;
	private String timestamp;
}
