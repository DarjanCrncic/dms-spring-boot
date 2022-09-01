package com.example.dms.api.dtos.content;

import com.example.dms.api.dtos.BaseEntityDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DmsContentDTO implements BaseEntityDTO{

	@Default
	private Long contentSize = 0l;
	@Default
	private String contentType = null;
	@Default
	private String originalFileName = null;

}
