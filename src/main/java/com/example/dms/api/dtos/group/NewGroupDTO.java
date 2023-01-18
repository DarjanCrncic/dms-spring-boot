package com.example.dms.api.dtos.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

import static com.example.dms.utils.Constants.MAX_LENGTH_32;
import static com.example.dms.utils.Constants.MIN_LENGTH_2;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewGroupDTO {

	@NotEmpty
	@Length(min = MIN_LENGTH_2, max = MAX_LENGTH_32)
	private String groupName;
	private String identifier;
	private String description;
}
