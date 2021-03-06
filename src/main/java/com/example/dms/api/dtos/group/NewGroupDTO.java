package com.example.dms.api.dtos.group;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewGroupDTO {

	@NotEmpty
	@Length(min = 5, max = 32)
	private String groupName;
	private String description;
}
