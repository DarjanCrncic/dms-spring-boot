package com.example.dms.api.dtos.document;

import com.example.dms.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ModifyDocumentDTO {

	@NotBlank
	@Length(min = Constants.MINLENGTH, max = 32, message = "Ivalid object name length, document name must have atleast " + Constants.MINLENGTH + " characters.")
	private String objectName;
	
	private String description;
	
	private List<String> keywords;
	
	private String type;
}
