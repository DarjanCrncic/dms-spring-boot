package com.example.dms.api.dtos.folder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import com.example.dms.utils.Constants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewFolderDTO {

	@NotEmpty
	@Pattern(regexp = Constants.FOLDER_PATH_REGEX)
	String path;
}
