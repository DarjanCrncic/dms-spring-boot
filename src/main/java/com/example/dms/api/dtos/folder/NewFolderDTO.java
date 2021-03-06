package com.example.dms.api.dtos.folder;

import java.util.UUID;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.example.dms.utils.Constants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewFolderDTO {

	@NotEmpty
	@Pattern(regexp = Constants.FOLDER_NAME_REGEX)
	String name;
	
	@NotNull
	UUID parentFolderId;
}
