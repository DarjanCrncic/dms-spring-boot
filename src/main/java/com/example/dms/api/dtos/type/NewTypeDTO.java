package com.example.dms.api.dtos.type;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import com.example.dms.utils.Constants;

import lombok.Data;

@Data
public class NewTypeDTO {
	
	@NotEmpty
	@Length(min = Constants.MINLENGTH, max = Constants.MAXLENGTH)
	private String typeName;
}
