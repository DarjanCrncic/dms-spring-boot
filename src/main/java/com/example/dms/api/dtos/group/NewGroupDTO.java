package com.example.dms.api.dtos.group;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewGroupDTO {

	private String groupName;
	private String description;
}
