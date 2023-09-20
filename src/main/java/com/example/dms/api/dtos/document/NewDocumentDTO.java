package com.example.dms.api.dtos.document;

import com.example.dms.utils.Constants;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class NewDocumentDTO {

	@NotBlank
	@Length(min = Constants.MIN_LENGTH_4, max = 32, message = "Ivalid object name length, document name must have atleast " + Constants.MIN_LENGTH_4 + " characters.")
	private String objectName;
	
	private String description;
	
	private List<String> keywords;
	
	private String type;
	
	@NotNull
	private Integer parentFolderId;

	private boolean rootFolder;
}
