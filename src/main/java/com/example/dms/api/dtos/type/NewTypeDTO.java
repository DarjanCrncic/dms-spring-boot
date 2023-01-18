package com.example.dms.api.dtos.type;

import com.example.dms.utils.Constants;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class NewTypeDTO {
	
	@NotEmpty
	@Length(min = Constants.MIN_LENGTH_4, max = Constants.MAX_LENGTH_32)
	private String typeName;
}
