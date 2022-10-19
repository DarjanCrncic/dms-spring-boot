package com.example.dms.api.dtos.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewGroupDTO {

	@NotEmpty
	@Length(min = 2, max = 32)
	private String groupName;
	private String identifier;
	private String description;
}
