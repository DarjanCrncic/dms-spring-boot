package com.example.dms.api.dtos.folder;

import com.example.dms.api.dtos.BaseEntityDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DmsFolderDTO implements BaseEntityDTO {

	private Integer id;
	private LocalDateTime creationDate;
	private LocalDateTime modifyDate;
	
	private String name;
	private Integer parentFolderId;
	@Default
	private List<Integer> subfolders = new ArrayList<>();
}
