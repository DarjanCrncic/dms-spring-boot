package com.example.dms.api.dtos.document;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.example.dms.utils.Constants;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewDocumentDTO {

	@NotBlank
	@Length(min = Constants.MINLENGTH, max = 32, message = "Ivalid object name length, document name must have atleast " + Constants.MINLENGTH + " characters.")
	private String objectName;
	
	private String description;
}
