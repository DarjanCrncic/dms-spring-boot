package com.example.dms.api.dtos.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static com.example.dms.utils.Constants.MAX_LENGTH_32;
import static com.example.dms.utils.Constants.MIN_LENGTH_4;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ModifyDocumentDTO {

	@NotBlank
	@Length(min = MIN_LENGTH_4, max = MAX_LENGTH_32, message = "Invalid object name length, document name must have at least "
			+ MIN_LENGTH_4 + " characters.")
	private String objectName;
	
	private String description;
	
	private List<String> keywords;
	
	private String type;
}
