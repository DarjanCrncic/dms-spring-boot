package com.example.dms.api.dtos.group;

import com.example.dms.api.dtos.BaseEntityDTO;
import com.example.dms.api.dtos.user.DmsUserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DmsGroupDTO implements BaseEntityDTO {
	
	private Integer id;
	private LocalDateTime creationDate;
	private LocalDateTime modifyDate;
	
	private String groupName;
	private String identifier;
	private String description;
	@Default
	private Set<DmsUserDTO> members = new HashSet<>();
}
