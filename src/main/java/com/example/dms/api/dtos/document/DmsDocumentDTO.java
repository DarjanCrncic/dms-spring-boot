package com.example.dms.api.dtos.document;

import com.example.dms.api.dtos.BaseEntityDTO;
import com.example.dms.api.dtos.content.DmsContentDTO;
import com.example.dms.api.dtos.user.DmsUserDTO;
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
public class DmsDocumentDTO implements BaseEntityDTO {

	private Integer id;
	private LocalDateTime creationDate;
	private LocalDateTime modifyDate;

	private String objectName;
	private String description;
	private DmsUserDTO creator;
	private String type;
	@Default
	private List<String> keywords = new ArrayList<>();
	private Integer parentFolderId;
	
	private DmsContentDTO content;
	
	private Integer rootId;
	private Integer predecessorId;
	private boolean immutable;
	private String version;
	private boolean branched;
}
