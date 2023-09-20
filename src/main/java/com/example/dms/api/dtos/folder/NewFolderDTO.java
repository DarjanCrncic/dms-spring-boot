package com.example.dms.api.dtos.folder;

import com.example.dms.utils.Constants;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
public class NewFolderDTO {

	@NotEmpty
	@Pattern(regexp = Constants.FOLDER_NAME_REGEX)
	String name;
	
	@NotNull
	Integer parentFolderId;

	private boolean rootFolder;
}
