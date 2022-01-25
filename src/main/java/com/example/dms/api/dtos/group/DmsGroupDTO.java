package com.example.dms.api.dtos.group;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.example.dms.domain.DmsUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DmsGroupDTO {
	
	private UUID id;
	private LocalDateTime creationDate;
	private LocalDateTime modifyDate;
	private String groupName;
	private String description;
	private Set<DmsUser> members;
}
